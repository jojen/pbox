package com.jojen;


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class PBox {

    private static Pin lovePin = RaspiPin.GPIO_06;
    private static Pin banPin = RaspiPin.GPIO_05;
    private static Pin nextPin = RaspiPin.GPIO_04;
    private static Pin pausePin = RaspiPin.GPIO_01;



    private static Pin loveLEDPin = RaspiPin.GPIO_29;
    private static Pin bannLEDPin = RaspiPin.GPIO_28;
    private static Pin nextLEDPin = RaspiPin.GPIO_27;
    private static Pin pauseLEDPin = RaspiPin.GPIO_26;

    public static void main(String[] args) throws IOException, InterruptedException {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput loveLED = gpio.provisionDigitalOutputPin(loveLEDPin, "LoveLED", PinState.HIGH);
        final GpioPinDigitalOutput bannLED = gpio.provisionDigitalOutputPin(bannLEDPin, "BannLED", PinState.HIGH);
        final GpioPinDigitalOutput nextLED = gpio.provisionDigitalOutputPin(nextLEDPin, "NextLED", PinState.HIGH);
        final GpioPinDigitalOutput pauseLED = gpio.provisionDigitalOutputPin(pauseLEDPin, "PauseLED", PinState.HIGH);

        glowLED(loveLED);
        glowLED(bannLED);
        glowLED(pauseLED);
        glowLED(nextLED);



        final Pianobar pianobar = new Pianobar();

        try {
            final GpioPinDigitalInput pauseButton = gpio.provisionDigitalInputPin(pausePin, PinPullResistance.PULL_DOWN);
            pauseButton.addListener((GpioPinListenerDigital) event -> {
                if (event.getState().isHigh()) {
                    glowLED(pauseLED);
                    pianobar.pause();

                }
            });

            final GpioPinDigitalInput nextButton = gpio.provisionDigitalInputPin(nextPin, PinPullResistance.PULL_DOWN);
            nextButton.addListener((GpioPinListenerDigital) event -> {
                if (event.getState().isHigh()) {
                    glowLED(nextLED);
                    pianobar.nextStation();
                }
            });

            final GpioPinDigitalInput loveButton = gpio.provisionDigitalInputPin(lovePin, PinPullResistance.PULL_DOWN);

            loveButton.addListener((GpioPinListenerDigital) event -> {
                if (event.getState().isHigh()) {
                    glowLED(loveLED);
                    pianobar.love();

                }
            });

            final GpioPinDigitalInput bannButton = gpio.provisionDigitalInputPin(banPin, PinPullResistance.PULL_DOWN);


            bannButton.addListener((GpioPinListenerDigital) event -> {
                    if (event.getState().isHigh()) {
                        glowLED(bannLED);
                        pianobar.bann();

                    }
            });

            for (; ; ) {
                Thread.sleep(5000);
                pianobar.heartbeat();
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
        led.low();
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(duration);
                led.high();

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