#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
from Adafruit_CharLCD import Adafruit_CharLCD

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(True)
from Constants import _Constants



GPIO.setup(_Constants.PAUSE_BUTTON_PIN, GPIO.IN)
GPIO.setup(_Constants.BAN_BUTTON_PIN, GPIO.IN)
GPIO.setup(_Constants.LOVE_BUTTON_PIN, GPIO.IN)
GPIO.setup(_Constants.NEXT_BUTTON_PIN, GPIO.IN)


lcd = Adafruit_CharLCD()

lcd.clear()
lcd.message("Hallo")

try:
    while 1:
        if GPIO.input(_Constants.PAUSE_BUTTON_PIN):
            print('pause')
            time.sleep(0.2)
        if GPIO.input(_Constants.NEXT_BUTTON_PIN):
            print('next')
            time.sleep(0.2)
        if GPIO.input(_Constants.BAN_BUTTON_PIN):
            print('ban')
            time.sleep(0.2)
        if GPIO.input(_Constants.LOVE_BUTTON_PIN):
            print('love')
            time.sleep(0.2)
        time.sleep(0.2)

except KeyboardInterrupt:
    lcd.clear()
    GPIO.cleanup()
    exit()