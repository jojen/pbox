import re
import subprocess, logging
import math
from Adafruit_CharLCD import Adafruit_CharLCD
import RPi.GPIO as GPIO

class PianobarHandler:

    def __init__(self):
        self.fifo = "/root/.config/pianobar/ctl"
        logging.info("init pianobar")
        self.pianobar_output = open("/opt/pbox/pianobar.out","r+")


    def start(self):
        #Start pianobar
        self.pianobar_output = open("/opt/pbox/pianobar.out","w")
        subprocess.Popen('aoss pianobar', shell=True, stdout = self.pianobar_output)

    def stop(self):
        print('Stopping pianobar!')
        #Stop pianobar
        self.writeFifo(command = 'q')


    def playPause(self):
        print('Play/Pause')
        self.writeFifo(command = 'p')

    def next(self):
        print('Next')
        self.writeFifo(command = 'n')

    def love(self):
        print('Love')
        self.writeFifo(command = '+')

    def ban(self):
        print('Ban')
        #Ban song for 1 month
        self.writeFifo(command = '-')

    def getSongInfo(self):

        text = self.pianobar_output.read()

        infoIter = re.finditer('\>.*by.*on.*', text)
        infoStr = None
        for item in infoIter:
            infoStr = item

        if infoStr is not None:
            infoStrSplit = infoStr.group(0).split()
            for index in range(len(infoStrSplit)):
                if infoStrSplit[index]=='by':
                    byIdx = index
                if infoStrSplit[index]=='on':
                    onIdx = index
            song = ' '.join(infoStrSplit[1:byIdx])
            song = song.replace('"','')
            artist = ' '.join(infoStrSplit[byIdx+1:onIdx])
            artist = artist.replace('"','')
            songInfo = [song,artist]

        else:
            songInfo = ['','']
        return songInfo


    def writeFifo(self, command):
        #Write a command to the pianobar FIFO
        fifo_w = open(self.fifo, 'w')
        fifo_w.write(command)
        fifo_w.close()

    def updateDisplay(self,songInfo,lcd,displayOld):
        display = ['',displayOld[1],displayOld[2]]
        #Song - line1
        if len(songInfo[0]) <= 16: #Center on screen
            padding = (16 - len(songInfo[0])) / 2
            line1 = (' ' * int(math.floor(padding))) + songInfo[0] + (' ' * int(math.ceil(padding)))
        else: #Scroll
            line1 = songInfo[0][display[1]:int(min(len(songInfo[0]),display[1]+16))]
            line1 = line1 + ' ' * int(16-len(line1))
            display[1]+= 1
            if display[1] == len(songInfo[0]):
                display[1] = 0
        #Artist - line2
        if len(songInfo[1]) <= 16: #Center on screen
            padding = (16 - len(songInfo[1])) / 2
            line2 = (' ' * int(math.floor(padding))) + songInfo[1] + (' ' * int(math.ceil(padding)))
        else: #Scroll
            line2 = songInfo[1][display[2]:int(min(len(songInfo[1]),display[2]+16))]
            line2 = line2 + ' ' * int(16-len(line2))
            display[2]+= 1
            if display[2] == len(songInfo[1]):
                display[2] = 0
        display[0] = str(line1 + '\n' + line2)
        if display[0] != displayOld[0]: #Only update if new
            lcd.clear()
            lcd.message(display[0])
        return display

LOVE_LED_PIN = 9
GPIO.setmode(GPIO.BCM)
GPIO.setup(LOVE_LED_PIN, GPIO.OUT)
GPIO.output(LOVE_LED_PIN, GPIO.LOW)



pb = PianobarHandler()
pb.updateDisplay(pb.getSongInfo(),Adafruit_CharLCD(),['',0,0])