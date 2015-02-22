#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
from Adafruit_CharLCD import Adafruit_CharLCD

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(True)

BAN_BUTTON_PIN = 27
BAN_LED_PIN = 22

LOVE_BUTTON_PIN = 5
LOVE_LED_PIN = 6

NEXT_BUTTON_PIN = 4
PAUSE_BUTTON_PIN = 17


GPIO.setup(PAUSE_BUTTON_PIN, GPIO.IN)
GPIO.setup(BAN_BUTTON_PIN, GPIO.IN)
GPIO.setup(LOVE_BUTTON_PIN, GPIO.IN)
GPIO.setup(NEXT_BUTTON_PIN, GPIO.IN)

GPIO.setup(BAN_LED_PIN, GPIO.OUT)
GPIO.setup(LOVE_LED_PIN, GPIO.OUT)

lcd = Adafruit_CharLCD()

lcd.clear()
lcd.message("Hallo")

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
            GPIO.output(BAN_LED_PIN, GPIO.HIGH)
            time.sleep(0.2)
        if GPIO.input(LOVE_BUTTON_PIN):
            print('love')
            GPIO.output(LOVE_LED_PIN, GPIO.HIGH)
            time.sleep(0.2)
        time.sleep(0.2)

except KeyboardInterrupt:
    lcd.clear()
    GPIO.cleanup()
    exit()