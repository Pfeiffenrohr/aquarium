#!/bin/sh
WORKDIR=$1
#exit 0
# IP address of AVR-NET-IO board and port number
AVRNETIO_IP=192.168.2.24
AVRNETIO_PORT=2701
Zeit=`date '+%H%M%S'`
datum=`date '+%Y%m%d'`
sql="insert into temperatur values(null,$datum,$Zeit"
# Get IDs of 1-Wire sensors
SENSOR_ID=`echo 1w list | nc -w 2 $AVRNETIO_IP $AVRNETIO_PORT | grep -v OK || exit 1`

for i in $SENSOR_ID
do
  # Initialize temperature measurement
  `echo 1w convert $i | nc -w 2 $AVRNETIO_IP $AVRNETIO_PORT 2>/dev/null | grep -v OK || exit 1`
  # Fetch measurement results
  TEMP=`echo 1w get $i | nc -w 2 $AVRNETIO_IP $AVRNETIO_PORT 2>/dev/null`
  # Print measurement results
  #echo "$i: $TEMP"
  sql="$sql,$TEMP"
  echo "$TEMP" >$WORKDIR/sensoren/$i
  echo "$Zeit $TEMP" >>$WORKDIR/sensoren/${i}_hist
  
done
sql="$sql,0.0)"
#echo $sql
mysql -u aquarium -paquarium -h $dbhost -D aquarium -e "$sql" >/dev/null 2>&1
exit 0
