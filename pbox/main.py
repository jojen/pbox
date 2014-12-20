#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
from Adafruit_CharLCD import Adafruit_CharLCD

lcd = Adafruit_CharLCD()

while 1:
    lcd.clear()
    lcd.begin(16,1)
    lcd.message('huhu\n')
    lcd.message('huhu2')
    time.sleep(20)
#LED_GPIO = 17
#B1_GPIO = 23



#GPIO.setmode(GPIO.BCM)

#GPIO.setup(LED_GPIO, GPIO.OUT)
#GPIO.setup(B1_GPIO, GPIO.IN)
#led_status = GPIO.input(LED_GPIO)

"""
while True:
    try:
        b1_state = GPIO.input(B1_GPIO)
        if b1_state == True:
            print ("huhu")
            if led_status == False:
                GPIO.output(LED_GPIO, GPIO.HIGH)
                led_status = True
            else:
                GPIO.output(LED_GPIO, GPIO.LOW)
                led_status = False
            time.sleep(0.2)

    except KeyboardInterrupt:
        GPIO.cleanup()
        exit()
"""