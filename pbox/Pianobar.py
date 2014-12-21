import re
import subprocess


class Pianobar:
    def __init__(self):
        #FIFO location
        self.fifo = "/root/.config/pianobar/ctl"

    def start(self):
        print('Starting pianobar!')
        #Create file for pianobar output...used to get artist names and song titles
        self.pianobar_output = open("pianobar.out","w")
        self.pianobar_output.close()
        self.pianobar_output = open("pianobar.out","r+")
        self.outfilePosition = 0
        #Start pianobar
        subprocess.Popen('aoss pianobar', shell=True, stdout = self.pianobar_output)

    def stop(self):
        print('Stopping pianobar!')
        #Stop pianobar
        self.writeFifo(command = 'q')
        #Close output file
        self.pianobar_output.close()

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
        #Get artist name and song name from output file
        self.pianobar_output.seek(self.outfilePosition)
        text = self.pianobar_output.read()
        self.outfilePosition = self.pianobar_output.tell()
        infoStr = re.search('\>.*by.*on.*', text)
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
