#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time

from Adafruit_CharLCD import Adafruit_CharLCD
from Pianobar import Pianobar

lcd = Adafruit_CharLCD()

GPIO.setmode(GPIO.BCM)

NEXT_BUTTON_PIN = 18

lcd.clear()

lcd.message("  Halli  Hallo\n")
lcd.message("gleich gehts los")

pb = Pianobar()
pb.start()

GPIO.setup(NEXT_BUTTON_PIN, GPIO.IN)


while 1:
    try:
        time.sleep(0.1)
        next_button_pressed = GPIO.input(NEXT_BUTTON_PIN)
        if next_button_pressed:
            pb.next()
            lcd.clear()
            print(pb.getSongInfo())
            lcd.message("next Song")
            time.sleep(0.5)



    except KeyboardInterrupt:
        lcd.clear()
        GPIO.cleanup()
        exit()


