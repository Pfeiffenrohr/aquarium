FROM ubuntu:latest

RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;
    
RUN apt-get update && \
    apt-get install -y netcat-openbsd &&\
    apt-get clean;
    
RUN apt-get update && \
    apt-get install -y mysql-client &&\
    apt-get clean;
    
 RUN apt-get update && \
    apt-get install -y postgresql-client &&\
    apt-get clean;   

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME
RUN export DEBIAN_FRONTEND=noninteractive
RUN apt-get install -y tzdata
RUN ln -fs /usr/share/zoneinfo/Europe/Berlin /etc/localtime
RUN dpkg-reconfigure --frontend noninteractive tzdata

RUN apt-get update && apt-get install -y dos2unix

#RUN cat "Europe/Berlin" > /etc/timezone
#RUN ln -sf /usr/share/zoneinfo/Europe/Berlin /etc/localtime

RUN mkdir /var/lib/aquarium
WORKDIR /var/lib/aquarium/
ADD target/aquarium-deamon-0.0.1-SNAPSHOT.jar /var/lib/aquarium/

RUN mkdir /var/lib/aquarium/sensoren
RUN mkdir /var/lib/aquarium/bin
RUN mkdir /var/lib/aquarium/config
COPY scripts/* /var/lib/aquarium/bin/
RUN chmod 777 /var/lib/aquarium/bin/*
RUN dos2unix /var/lib/aquarium/bin/*

RUN cd /var/lib/aquarium/sensoren && ln -s 100e3e9002080016 sensor1

COPY lib/*.jar /var/lib/aquarium/
COPY target/lib/*.jar /var/lib/aquarium/
ENTRYPOINT ["/var/lib/aquarium/bin/startDeamon.sh"]
