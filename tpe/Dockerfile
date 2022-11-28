FROM eliottclavier/flutter-builder:latest

# Configuration for shop project
ENV TPE=/tpe
RUN mkdir $TPE
WORKDIR $TPE
RUN mkdir /apk
COPY . .

RUN yum install -y dos2unix
RUN dos2unix /tpe/entrypoint.sh

CMD bash entrypoint.sh