#!/bin/bash
cd /var/lib/aquarium
/usr/bin/java -classpath /var/lib/aquarium/aquarium-deamon-0.0.1-SNAPSHOT.jar:/var/lib/aquarium/mysql.jar  aquarium/Aquarium  /var/lib/aquarium/config/auarium.cfg