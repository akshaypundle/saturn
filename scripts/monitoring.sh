#!/bin/sh
echo "" > /tmp/aa;
curl -m 20  http://localhost/ > /tmp/aa;
actualsize=$(wc -c /tmp/aa | awk {'print $1'})
if [ $actualsize -ge 100 ]; then
  echo "ok"
else
  killall python;  
  cd /home/ec2-user/webserver &&  nohup python -m SimpleHTTPServer 80
fi

