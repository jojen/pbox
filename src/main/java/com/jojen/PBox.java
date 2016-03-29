package com.jojen;

/* Pi4J imports */

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
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

    public static void main(String[] args) throws IOException {
        final GpioController gpio = GpioFactory.getInstance();
        final Pianobar pianobar = new Pianobar();

        try {

            final GpioPinDigitalInput pauseButton = gpio.provisionDigitalInputPin(pausePin, PinPullResistance.PULL_DOWN);
            pauseButton.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if (event.getState().isHigh()) {
                        pianobar.playPause();
                    }
                }

            });

            final GpioPinDigitalInput nextButton = gpio.provisionDigitalInputPin(nextPin, PinPullResistance.PULL_DOWN);
            nextButton.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if (event.getState().isHigh()) {
                        pianobar.next();
                    }
                }

            });

            final GpioPinDigitalInput loveButton = gpio.provisionDigitalInputPin(lovePin, PinPullResistance.PULL_DOWN);
            final GpioPinDigitalOutput loveLED = gpio.provisionDigitalOutputPin(loveLEDPin, "LoveLED", PinState.LOW);

            loveButton.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if (event.getState().isHigh()) {
                        pianobar.love();
                        loveLED.high();
                        Thread t1 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                    loveLED.low();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t1.start();
                    }
                }

            });

            final GpioPinDigitalInput bannButton = gpio.provisionDigitalInputPin(banPin, PinPullResistance.PULL_DOWN);
            final GpioPinDigitalOutput bannLED = gpio.provisionDigitalOutputPin(bannLEDPin, "BannLED", PinState.LOW);

            bannButton.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                    if (event.getState().isHigh()) {
                        pianobar.bann();
                        bannLED.high();
                        Thread t1 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                    bannLED.low();

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t1.start();
                    }
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
}