# This code shows a modification to the original version written by Ubaldo Iván Blanco Covarrubias (ublanco@cicese.edu.mx)
#
# The file lets read the data received by the physically connected MOTE.
#
# Code adapted for particular purposes by:
# Franceli Linney Cibrian Roble - linney11@gmail.com
# Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com

import serial
from serial import *
import urllib
from urllib import *
import sqlite3
from sqlite3 import *
import datetime
from datetime import *



ser = 	serial.Serial(port='/dev/ttyUSB0', 
	baudrate=115200,
	bytesize=serial.EIGHTBITS,
	parity=serial.PARITY_NONE,
	stopbits=serial.STOPBITS_ONE,
	timeout=None,
	xonxoff=0,
	rtscts=0,
	interCharTimeout=None)

conn = sqlite3.connect("sensing.sqlite") # or use :memory: to put it in RAM
cursor = conn.cursor()

while 1:
	line=ser.readline()

	ubisoaCode=line[0:2]
    	nodeId=line[2:4]
	optionSensing=line[4:6]
	data=line[-4:]
	

    	if ubisoaCode=="##":
		now = datetime.datetime.now()
		
		if optionSensing=="02":
			cursor.execute("INSERT INTO hum VALUES ('"+nodeId+"', '"+data+"', '"+datetime.time(now.hour, now.minute, now.second)+"', '0', '0')")	
			
		if optionSensing=="01":
			cursor.execute("INSERT INTO temp VALUES ('"+nodeId+"', '"+data+"', '"+datetime.time(now.hour, now.minute, now.second)+"', '0', '0')")

		conn.commit()		
	print ubisoaCode+" "+ nodeId + " " + optionSensing + " " + data
    
    #To send a POST
    #cons=urllib.urlopen("http://xxx.xxx.xxx.xxx/xxx.php?id_p=102&x_pix="+x_pix")
