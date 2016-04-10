package com.jojen;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class PBox {

    private static Pin pausePin = RaspiPin.GPIO_07;
    private static Pin nextPin = RaspiPin.GPIO_00;
    private static Pin lovePin = RaspiPin.GPIO_02;
    private static Pin banPin = RaspiPin.GPIO_21;

    private static Pin loveLEDPin = RaspiPin.GPIO_03;
    private static Pin bannLEDPin = RaspiPin.GPIO_22;

    public static void main(String[] args) throws IOException, InterruptedException {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput loveLED = gpio.provisionDigitalOutputPin(loveLEDPin, "LoveLED", PinState.LOW);
        final GpioPinDigitalOutput bannLED = gpio.provisionDigitalOutputPin(bannLEDPin, "BannLED", PinState.LOW);

        glowLED(loveLED);
        glowLED(bannLED);



        final Pianobar pianobar = new Pianobar(new LCD());

        try {
            final GpioPinDigitalInput pauseButton = gpio.provisionDigitalInputPin(pausePin, PinPullResistance.PULL_DOWN);
            pauseButton.addListener((GpioPinListenerDigital) event -> {
                if (event.getState().isHigh()) {
                    pianobar.playPause();
                }
            });

            final GpioPinDigitalInput nextButton = gpio.provisionDigitalInputPin(nextPin, PinPullResistance.PULL_DOWN);
            nextButton.addListener((GpioPinListenerDigital) event -> {
                if (event.getState().isHigh()) {
                    pianobar.next();
                }
            });

            final GpioPinDigitalInput loveButton = gpio.provisionDigitalInputPin(lovePin, PinPullResistance.PULL_DOWN);

            loveButton.addListener((GpioPinListenerDigital) event -> {
                if (event.getState().isHigh()) {
                    pianobar.love();
                    glowLED(loveLED);
                }
            });

            final GpioPinDigitalInput bannButton = gpio.provisionDigitalInputPin(banPin, PinPullResistance.PULL_DOWN);


            bannButton.addListener((GpioPinListenerDigital) event -> {
                    if (event.getState().isHigh()) {
                        pianobar.bann();
                        glowLED(bannLED);
                    }
            });

            for (; ; ) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("interrupted");
        } finally {
            pianobar.stop();
            if (gpio != null) {
                gpio.shutdown();
            }
        }
    }

    private static void glowLED(GpioPinDigitalOutput led, int duration) {
        led.high();
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(duration);
                led.low();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }
    private static void glowLED(GpioPinDigitalOutput led) {
        glowLED(led,1);
    }

}