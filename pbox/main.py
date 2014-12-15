#!/usr/bin/python
# -*- coding: utf-8 -*-  
import RPi.GPIO as GPIO
import time

LED = 11

GPIO.setmode(GPIO.BOARD)
# need to set up every channel which are using as an input or an output  

GPIO.setup(LED, GPIO.OUT)

while True:
    GPIO.output(LED, GPIO.HIGH)
    time.sleep(2)
    GPIO.output(LED, GPIO.LOW)
    time.sleep(2)
