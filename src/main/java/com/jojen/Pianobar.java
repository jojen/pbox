package com.jojen;

/**
 * Created by Jochen on 17.04.2016.
 */


import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static net.sf.expectit.matcher.Matchers.contains;

public class Pianobar {


    private Expect pianobar;
    private String runDir = "/opt/pbox/run/";
    private File stationFile = new File(runDir + "current_station");
    private String nowPlaying = runDir + "nowplaying";
    private Integer stationId = 0;
    private Long lastUserInteractionTime;
    private Long startTime;
    private boolean isPause = true;
    private String currentArtist = null;
    private LCD lcd;

    Process process;
    private boolean isStarted;

    Pianobar() {
        try {
            initStationId();
            lcd = new LCD();
            lcd.show("Hallo", "", LCD.KEY_MESSAGE);
            lastUserInteractionTime = System.currentTimeMillis();
            waitUntilConnected();
            startProcess();
            pianobar.expect(contains("Select station:"));
            isStarted = true;
            pianobar.sendLine(stationId + "");
            updateSongOnDisplay();
            isPause = false;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startProcess() throws IOException {
        startTime = System.currentTimeMillis();
        ProcessBuilder builder = new ProcessBuilder("/usr/bin/pianobar");

        process = builder.start();
        pianobar = new ExpectBuilder()
                .withInputs(process.getInputStream())
                .withOutput(process.getOutputStream())
                .withTimeout(25, TimeUnit.SECONDS)
                .withExceptionOnFailure()
                .withEchoInput(System.err)
                .withEchoOutput(System.out)
                .withExceptionOnFailure()
                .build();
    }


    public void nextStation() {
        Long currentTime = System.currentTimeMillis();
        if (currentTime - lastUserInteractionTime > 5000) {
            try {
                pianobar.send("s");
                String lastRow = pianobar.expect(contains("QuickMix")).getBefore();
                int maxStations = getMaxStations(lastRow);
                stationId = stationId + 1;
                if (stationId > maxStations) {
                    stationId = 0;
                }
                pianobar.expect(contains("Select station:"));
                pianobar.sendLine(stationId + "");
                lastUserInteractionTime = currentTime;
                writeStationId();
                updateStationOnDisplay();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        if (!isPause) {
            isPause = true;
            lcd.show("Pausiere", "", LCD.KEY_MESSAGE);
        } else {
            isPause = false;
            currentArtist = null;
            updateSongOnDisplay();

        }
        try {
            pianobar.send("p");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void love() {
        try {
            pianobar.send("+");
            lcd.show("Super Song :)", "", LCD.KEY_MESSAGE);
            currentArtist = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bann() {
        try {
            pianobar.send("-");
            lcd.show("Verbannt !", "", LCD.KEY_MESSAGE);
            currentArtist = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStationOnDisplay() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(nowPlaying));
            String stationName = prop.getProperty("station" + stationId);
            lcd.show(stationName, "", LCD.KEY_STATION_CAHNGE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void heartbeat(){
        if(!isStarted && System.currentTimeMillis() - startTime > 10000){
            try {
                process.destroy();
                pianobar.close();
                startProcess();
                pianobar.expect(contains("Select station:"));
                isStarted = true;
                pianobar.sendLine(stationId + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateSongOnDisplay();
    }


    public void updateSongOnDisplay() {
        if (!isPause) {
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(nowPlaying));
                String artist = prop.getProperty("artist");
                String title = prop.getProperty("title");
                if (!artist.equals(currentArtist)) {
                    lcd.show(artist, title, "");
                    currentArtist = artist;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void initStationId() throws IOException {
        if (!stationFile.exists()) {
            stationFile.createNewFile();
        } else {
            BufferedReader brTest = new BufferedReader(new FileReader(stationFile));
            String text = brTest.readLine();
            stationId = Integer.valueOf(text);
        }
    }

    private int getMaxStations(String lastRow) {
        for (int i = lastRow.length() - 1; i > 0; i--) {
            char currentChar = lastRow.charAt(i);
            char secondChar = lastRow.charAt(i - 1);
            if (Character.isDigit(currentChar)) {
                if (Character.isDigit(secondChar)) {
                    return Integer.parseInt(lastRow.substring(i - 1, i + 1));
                }
                return Integer.parseInt(lastRow.substring(i, i + 1));
            }
        }
        return 1;
    }

    private void writeStationId() {
        try {
            Writer wr = new FileWriter(stationFile);
            wr.write(String.valueOf(stationId));
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            pianobar.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitUntilConnected() {
        boolean connected = false;
        int i = 0;
        while (!connected) {
            Socket socket = null;
            try {
                socket = new Socket("www.pandora.com", 80);
                connected = socket.isConnected();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!connected) {
                try {
                    Thread.sleep(1000);
                    i++;
                    lcd.show("keine Verbindung", "bereits " + i + " mal versucht","");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }
}