package com.jojen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jochen on 28.03.2016.
 */
public class Pianobar {
    private LCD lcd;
    private Process pianobarProcess = null;
    private BufferedWriter ctl = null;
    private BufferedWriter pianobarIn = null;
    private BufferedReader pianobarOut = null;
    private int stationId = 0;
    File stationFile = new File("/opt/pbox/current_station.txt");
    private boolean isPause = false;


    Pianobar(LCD lcd) throws IOException {
        try {
            this.lcd = lcd;
            System.out.println("start process");
            pianobarProcess = new ProcessBuilder("/usr/bin/pianobar").start();
            FileWriter fileWriter = new FileWriter(new File("/root/.config/pianobar/ctl"), true);
            ctl = new BufferedWriter(fileWriter);
            pianobarOut = new BufferedReader(new
                    InputStreamReader(pianobarProcess.getInputStream()));
            pianobarIn = new BufferedWriter(new
                    OutputStreamWriter(pianobarProcess.getOutputStream()));

            // Wir holen uns die persistierte station ID
            if (!stationFile.exists()) {
                stationFile.createNewFile();
            } else {
                BufferedReader brTest = new BufferedReader(new FileReader(stationFile));
                String text = brTest.readLine();
                stationId = Integer.valueOf(text);
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    void playPause() {
        System.out.println("play / pause");
        try {
            pianobarIn.write("p");
            pianobarIn.flush();
            if (!isPause) {
                isPause = true;
                lcd.show("Pause",LCD.KEY_MESSAGE);
            } else {
                isPause = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void next() {
        System.out.println("next");
        try {
            pianobarIn.write("s");
            pianobarIn.flush();
            Thread.sleep(100);

            boolean stationStarted = false;
            List<String> stations = new ArrayList<>();

            byte[] inputData = new byte[1024];
            int readCount = readInputStreamWithTimeout(pianobarProcess.getInputStream(), inputData, 100);
            String s = new String(inputData);

            for (String line : s.split("\t")) {
                if (line.contains("0) ")) {
                    stationStarted = true;
                }
                if (stationStarted) {
                    line = line.replaceAll("[ ]+[0-9]+\\)[ ]+[Q,q][ ]+", "");
                    line = line.replaceAll("\n\u001B\\[2K", "");
                    stations.add(line);
                }
            }
            if (stations.size() > 0) {
                if (stationId < stations.size() - 1) {
                    stationId++;
                } else {
                    stationId = 0;
                }

                lcd.show(stations.get(stationId),LCD.KEY_STATION_CAHNGE);

                System.out.println("change station to " + stations.get(stationId));
                pianobarIn.write(stationId + "\n\t\n");
                pianobarIn.flush();
                PrintWriter pw = new PrintWriter(new FileWriter(stationFile));
                pw.write("" + stationId);
                pw.close();
            } else {
                lcd.show("no station found");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    void love() {
        System.out.println("love");
        try {
            pianobarIn.write("+");
            pianobarIn.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void bann() {
        System.out.println("bann");
        try {
            pianobarIn.write("-");
            pianobarIn.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stop() {
        System.out.println("stop");
        pianobarProcess.destroy();
    }

    private int readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis)
            throws IOException {
        int bufferOffset = 0;
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < b.length) {
            int readLength = java.lang.Math.min(is.available(), b.length - bufferOffset);
            // can alternatively use bufferedReader, guarded by isReady():
            int readResult = is.read(b, bufferOffset, readLength);
            if (readResult == -1) break;
            bufferOffset += readResult;
        }
        return bufferOffset;
    }

}
