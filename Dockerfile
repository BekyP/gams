FROM ubuntu:20.04

USER root

SHELL ["/bin/bash", "-c"]

# git install
RUN apt-get update -y
RUN apt-get install -y git

# copy gams files
COPY gams-install-dir/gamsFiles /opt/gams/gamsFiles
COPY gams-install-dir/gamsJavaApi /opt/gams/gamsJavaApi

# install gams
WORKDIR /opt/gams
RUN ls -la /opt/gams
RUN ls -la /opt/gams/gamsFiles
RUN /opt/gams/gamsFiles/linux_x64_64_sfx.exe

# remove install file
RUN rm /opt/gams/gamsFiles/linux_x64_64_sfx.exe

# install java via sdkman
RUN apt-get update -y && apt-get install -y --no-install-recommends wget curl zip unzip ca-certificates
RUN mkdir /opt/sdkman
WORKDIR /opt/sdkman

RUN curl "https://get.sdkman.io" | bash
RUN chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh"
RUN source "$HOME/.sdkman/bin/sdkman-init.sh"

# workaround for current bash session in docker build
RUN chmod +x /opt/gams/gamsFiles/docker-install-java.sh
RUN /opt/gams/gamsFiles/docker-install-java.sh

ENV JAVA_HOME /root/.sdkman/candidates/java/current/bin/java
RUN ln -s /root/.sdkman/candidates/java/current/bin/java /bin/java
