package com.jojen;

import java.io.*;

/**
 * Created by Jochen on 28.03.2016.
 */
public class Pianobar {
    private Process pianobarProcess = null;

    private BufferedWriter ctl = null;

    Pianobar() {
        try {
            System.out.println("start process");
            pianobarProcess = Runtime.getRuntime().exec("/usr/bin/pianobar");
            FileWriter fileWriter = new FileWriter(new File("/root/.config/pianobar/ctl"), true);
            ctl = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void playPause() {
        System.out.println("play / pause");
        try {
            ctl.write("p");
            ctl.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void next() {
        System.out.println("next");
        try {
            ctl.write("p");
            ctl.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void love() {
        System.out.println("love");
        try {
            ctl.write("p");
            ctl.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void bann() {
        System.out.println("bann");
        try {
            ctl.write("p");
            ctl.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void stop() {
        System.out.println("stop");
        pianobarProcess.destroy();
    }

}
