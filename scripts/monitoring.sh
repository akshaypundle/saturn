#!/bin/sh
if [ "$#" -ne 1 ]; then
    echo "Usage: monitoring.sh <path of web root>"
    exit
fi

echo "" > /tmp/aa;
curl -m 20  http://localhost/ > /tmp/aa;
actualsize=$(wc -c /tmp/aa | awk {'print $1'})
if [ $actualsize -ge 100 ]; then
  echo "ok"
else
  killall python;  
  cd $1 &&  nohup python -m SimpleHTTPServer 80
fi

