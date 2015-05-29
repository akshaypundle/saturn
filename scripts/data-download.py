#!/usr/bin/python

import urllib2
import re
import os.path
import time
import sys

from sets import Set
from stripogram import html2text
import pandas as pd
from pandas.io.data import Options, RemoteDataError

if len(sys.argv) != 2:
    print "Usage: data-download.py <base dir>"
    sys.exit(1)
    
baseDir = sys.argv[1]+"/"
blankLine = re.compile("^\s*$");
ignoreLine = re.compile("^\s*#");
tickersFile = open(baseDir+"/tickers")
tickersList = []
startTickerIndex = 0
downloaded = set()

for line in tickersFile:
  if not blankLine.match(line) and not ignoreLine.match(line):
    ticker = line.strip()
    tickersList.append(ticker);

if os.path.isfile("last-downloaded"):
  last = open(baseDir+"last-downloaded")
  lastTicker = last.readline().strip()
  try:
    startTickerIndex = tickersList.index(lastTicker) + 1
  except ValueError:
    startTickerIndex = 0

  if startTickerIndex >= len(tickersList):
    startTickerIndex = 0

for i in range(startTickerIndex, len(tickersList)):
  ticker = tickersList[i]

  if ticker in downloaded:
    print "Already downloaded",ticker," ... Skipping."
    continue

  print "Downloading", ticker, "...",

  # download the data
  toDownload = Options(ticker, "yahoo")
  try:
    data = toDownload.get_options_data();
    pd.DataFrame(data).to_csv(baseDir+"/data/" + ticker)
    print "done."
  except (ValueError, RemoteDataError,  StopIteration) as e:
    print "skipped."
    print e

  downloaded.add(ticker)
  last = open(baseDir+"last-downloaded", "w")
  if i != len(tickersList) - 1:
    last.write(ticker)
  last.close()
  time.sleep(0.1)

