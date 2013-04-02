#!/opt/local/bin/python2.7
# encoding: utf-8
"""
couchimport.py
Created by Marian Steinbach on 2011-11-10
Modified by Daniel Zhao for NOAA weather stations import.
"""

import sys
import os
# import re
import csv
from couchdbkit import *


class Station(Document):
	station_id = StringProperty()
	state = StringProperty()
	station_name = StringProperty()
	latitude = StringProperty()
	longitude = StringProperty()
	station_urls = ListProperty()


def main():
	server = Server('http://username:password@gutiela.iriscouch.com')
	db = server.get_or_create_db("solazo-weather-station")
	Station.set_db(db)
	linecount = 0
	stations = []
#	matcher = re.compile(r'"([0-9]{10})";"([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})";"([^\"]+)";"([^\"]+)"')
	csvfile = open('noaa-stationlist_pre_import.csv', 'rb')
	stationreader = csv.reader(csvfile, delimiter=',')
	for line in stationreader:
#		matches = matcher.match(line)
		ss = Station(
			station_id = line[0],
			state = line[1],
			station_name = line[2],
			latitude = line[3],
			longitude = line[4],
			station_urls = [line[5], line[6], line[7] ]
		)
		stations.append(ss)
		linecount += 1
		if linecount % 1000 == 0:
			db.bulk_save(stations)
			stations = []
	db.bulk_save(stations)
	print linecount, "rows written"


if __name__ == '__main__':
	main()
