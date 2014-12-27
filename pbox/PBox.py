#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
import logging
from PianobarHandler import PianobarHandler

# Logging configuration
logging.basicConfig(filename="/opt/pbox/pianobar-handler.log", level=logging.INFO)


GPIO.setmode(GPIO.BCM)
NEXT_BUTTON_PIN = 18

logging.info("start pianobarhandler")
pb = PianobarHandler()
pb.start()


GPIO.setup(NEXT_BUTTON_PIN, GPIO.IN)


while 1:
    try:
        time.sleep(0.1)
        next_button_pressed = GPIO.input(NEXT_BUTTON_PIN)
        if next_button_pressed:
            pb.next()

    except KeyboardInterrupt:
        GPIO.cleanup()
        exit()
