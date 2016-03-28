package com.jojen;

import java.io.*;

/**
 * Created by Jochen on 28.03.2016.
 */
public class Pianobar {
    Process pianobarProcess = null;

    BufferedWriter ctl = null;

    public Pianobar() {
        try {
            System.out.println("start process");
            pianobarProcess = Runtime.getRuntime().exec("/usr/bin/pianobar");
            FileWriter fileWriter = new FileWriter(new File("/root/.config/pianobar/ctl"), true);
            ctl = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playPause() throws IOException {
        System.out.println("play / pause");
        ctl.write("p");
        ctl.flush();
    }

    public void stop() {
        System.out.println("stop");
        pianobarProcess.destroy();
    }

}
