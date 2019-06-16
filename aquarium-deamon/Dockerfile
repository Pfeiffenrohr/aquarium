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

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

RUN apt-get update && apt-get install -y dos2unix

RUN mkdir /var/lib/aquarium
WORKDIR /var/lib/aquarium/
ADD target/aquarium-deamon-0.0.1-SNAPSHOT.jar /var/lib/aquarium/

RUN mkdir /var/lib/aquarium/bin
COPY scripts/* /var/lib/aquarium/bin/
RUN chmod 777 /var/lib/aquarium/bin/*
RUN dos2unix /var/lib/aquarium/bin/*


COPY lib/*.jar /var/lib/aquarium/
COPY target/lib/*.jar /var/lib/aquarium/
ENTRYPOINT ["/var/lib/aquarium/bin/startDeamon.sh"]
