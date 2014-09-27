#!/usr/bin/python

import urllib2
import re
import os.path
import time

from sets import Set
from stripogram import html2text
from pandas.io.data import Options, RemoteDataError

blankLine = re.compile("^\s*$");
ignoreLine = re.compile("^\s*#");
tickersFile = open("tickers")
tickersList = []
startTickerIndex = 0
downloaded = set()

for line in tickersFile:
  if not blankLine.match(line) and not ignoreLine.match(line):
    ticker = line.strip()
    tickersList.append(ticker);

if os.path.isfile("last-downloaded"):
  last = open("last-downloaded")
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
    data.to_csv("data/" + ticker)
    print "done."
  except (ValueError, RemoteDataError,  StopIteration) as e:
    print "skipped."

  downloaded.add(ticker)
  last = open("last-downloaded", "w")
  if i != len(tickersList) - 1:
    last.write(ticker)
  last.close()
  time.sleep(0.1)

