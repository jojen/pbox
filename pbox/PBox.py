#!/usr/bin/python
# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
import logging
import sys
from PianobarHandler import PianobarHandler
from Constants import _Constants


# Logging configuration

logger = logging.getLogger('pbox')
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler('/opt/pbox/pbox.log')
fh.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)
logger.addHandler(fh)


GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)




pb = PianobarHandler()


def startPianobar():
    logger.info("start pbox")
    pb.start()

def update():
    GPIO.setup(_Constants.LOVE_LED_PIN, GPIO.OUT)
    GPIO.output(_Constants.LOVE_LED_PIN, GPIO.LOW)
    pb.updateDisplay(pb.getSongInfo(),['',0,0])

def init():
    global state_pause, next_button_pressed, pause_button_pressed, love_button_pressed, ban_button_pressed
    GPIO.setup(_Constants.NEXT_BUTTON_PIN, GPIO.IN)
    GPIO.setup(_Constants.PAUSE_BUTTON_PIN, GPIO.IN)
    GPIO.setup(_Constants.LOVE_BUTTON_PIN, GPIO.IN)
    GPIO.setup(_Constants.BAN_BUTTON_PIN, GPIO.IN)
    GPIO.setup(_Constants.LCD_BACKLIGHT_PIN, GPIO.OUT)
    GPIO.output(_Constants.LCD_BACKLIGHT_PIN, GPIO.HIGH)
    GPIO.setup(_Constants.LOVE_LED_PIN, GPIO.OUT)
    GPIO.output(_Constants.LOVE_LED_PIN, GPIO.LOW)
    GPIO.setup(_Constants.BAN_LED_PIN, GPIO.OUT)
    GPIO.output(_Constants.BAN_LED_PIN, GPIO.LOW)
    state_pause = False
    while 1:
        try:
            time.sleep(0.1)
            next_button_pressed = GPIO.input(_Constants.NEXT_BUTTON_PIN)
            pause_button_pressed = GPIO.input(_Constants.PAUSE_BUTTON_PIN)
            love_button_pressed = GPIO.input(_Constants.LOVE_BUTTON_PIN)
            ban_button_pressed = GPIO.input(_Constants.BAN_BUTTON_PIN)

            if next_button_pressed:
                logger.debug("next station button pressed")
                pb.changeStation()
                GPIO.output(_Constants.LOVE_LED_PIN, GPIO.LOW)
                time.sleep(0.7)

            if pause_button_pressed:
                logger.debug("pause/play button pressed")
                pb.playPause()
                if state_pause:
                    state_pause = False
                    GPIO.output(_Constants.LCD_BACKLIGHT_PIN, GPIO.HIGH)

                else:
                    state_pause = True
                    GPIO.output(_Constants.LCD_BACKLIGHT_PIN, GPIO.LOW)
                time.sleep(0.7)

            if love_button_pressed:
                logger.debug("love button pressed")
                GPIO.output(_Constants.LOVE_LED_PIN, GPIO.HIGH)
                pb.love()
                time.sleep(0.7)

            if ban_button_pressed:
                logger.debug("ban button pressed")
                GPIO.output(_Constants.BAN_LED_PIN, GPIO.HIGH)
                pb.ban()
                time.sleep(2)
                GPIO.output(_Constants.BAN_LED_PIN, GPIO.LOW)

        except KeyboardInterrupt:
            GPIO.cleanup()
            exit()

try:

    if len(sys.argv) == 1:
        startPianobar()
        init()

    if len(sys.argv) == 2:
        if sys.argv[1] == "songstart":
            update()

except Exception as e:
    logger.exception(e)
