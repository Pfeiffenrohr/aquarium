#!/bin/bash
cd /var/lib/aquarium
/usr/bin/java -classpath /var/lib/aquarium/aquarium-deamon-0.0.1-SNAPSHOT.jar:/var/lib/aquarium/postgresql-42.2.4.jar:/var/lib/aquarium/bmw-1.0.jar aquarium/Aquarium  /var/lib/aquarium/config/aquarium.cfg