#!/bin/bash
WORKDIR=$1
AVRNETIO_IP=192.168.2.24
AVRNETIO_PORT=2701
dist=`echo adc get 4 | nc -w 2 $AVRNETIO_IP $AVRNETIO_PORT`
wert=`echo $((0x$dist))`
echo $wert > $WORKDIR/sensoren/wasserstand.txt
exit 0
