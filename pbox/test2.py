#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
from Adafruit_CharLCD import Adafruit_CharLCD

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(True)

BAN_BUTTON_PIN = 7
LOVE_BUTTON_PIN = 8

NEXT_BUTTON_PIN = 9
PAUSE_BUTTON_PIN = 11


GPIO.setup(PAUSE_BUTTON_PIN, GPIO.IN)
GPIO.setup(BAN_BUTTON_PIN, GPIO.IN)
GPIO.setup(LOVE_BUTTON_PIN, GPIO.IN)
GPIO.setup(NEXT_BUTTON_PIN, GPIO.IN)


#lcd = Adafruit_CharLCD()

#lcd.clear()
#lcd.message("Hallo")

try:
    while 1:
        if GPIO.input(PAUSE_BUTTON_PIN):
            print('pause')
            time.sleep(0.2)
        if GPIO.input(NEXT_BUTTON_PIN):
            print('next')
            time.sleep(0.2)
        if GPIO.input(BAN_BUTTON_PIN):
            print('ban')
            time.sleep(0.2)
        if GPIO.input(LOVE_BUTTON_PIN):
            print('love')
            time.sleep(0.2)
        time.sleep(0.2)

except KeyboardInterrupt:
   # lcd.clear()
    GPIO.cleanup()
    exit()