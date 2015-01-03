from random import randint
import re
import subprocess, logging
import math, time
from Adafruit_CharLCD import Adafruit_CharLCD
import configparser, itertools


class PianobarHandler:

    def __init__(self):
        self.fifo = "/root/.config/pianobar/ctl"
        self.pianobar_output = open("/opt/pbox/pianobar.out","r+")
        self.logger = logging.getLogger('pbox')
        self.lcd = Adafruit_CharLCD()
        self.config = configparser.ConfigParser()


    def start(self):
        self.logger.debug("start subprocess pianobar")
        #Start pianobar
        self.pianobar_output = open("/opt/pbox/pianobar.out","w")
        subprocess.Popen('aoss pianobar', shell=True, stdout = self.pianobar_output)

    def stop(self):
        #Stop pianobar
        self.writeFifo(command = 'q')


    def playPause(self):
        self.writeFifo(command = 'p')

    def next(self):
        self.writeFifo(command = 'n')

    def love(self):
        self.writeFifo(command = '+')

    def ban(self):
        #Ban song for 1 month
        self.writeFifo(command = '-')

    def changeStation(self):
        self.logger.debug("change station")
        f = open("/opt/pbox/pianobar.out",'r+')
        f.truncate()

        self.writeFifo(command = 's')
        time.sleep(1.5)
        stationtext = f.read()

        i = 0
        haveStations = True
        stations = []
        while haveStations:
            if str(i)+")" in stationtext:
                stationtext = stationtext.split(str(i)+")")[1]
                i += 1

                if "\n" in stationtext:
                    station = stationtext.split("\n")[0]
                    stations.append(re.sub('\s{1,}[q,Q]\s{1,}', '', station))
            else:
                haveStations = False

        self.logger.info(stations)
        if len(stations) > 0:
            try:
                stationFile = open("/opt/pbox/station","r")

            except IOError:
                stationFile = open("/opt/pbox/station",'w+')
                stationFile.close()
                stationFile = open("/opt/pbox/station","r")


            numberCandidate = stationFile.read()
            number = randint(0, len(stations)-1)
            self.logger.debug("current station number:"+ numberCandidate)
            if numberCandidate.isdigit():
                nr= int(numberCandidate) + 1
                if nr >= len(stations):
                    number = 0
                else:
                    number = nr
            stationFile = open("/opt/pbox/station",'w')
            stationFile.truncate()
            stationFile.write(str(number))
            stationFile.close()

            pos = 16
            station = list(stations[number])
            if len(station) > pos:
                while pos > 0:
                    if station[pos] == ' ':
                        station[pos] = '\n'
                        pos = 0
                    pos -= 1

            self.lcd.clear()
            self.lcd.message("".join(station))
            self.writeFifo(command = str(number))
            self.writeFifo(command = '\n')
            # jetzt speichern wir das
            time.sleep(2)
            stationGroup = re.search("Station \".*\" \((.*)\)", f.read())
            if stationGroup is not None:
                stationId = stationGroup.group(1)
                self.logger.info("set new autostart station: "+stationId)
                f = open("/root/.config/pianobar/config",'r')
                config = f.read()
                f.close()


                newConfig = re.sub("autostart_station = ([0-9].*)","autostart_station = "+stationId,config)
                f = open("/root/.config/pianobar/config",'w')
                f.write(newConfig)
                f.close()



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

    def updateDisplay(self,songInfo,displayOld):
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
            self.lcd.clear()
            self.lcd.message(display[0])
        return display