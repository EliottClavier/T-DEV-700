FROM eliottclavier/flutter-builder:latest

# Configuration for shop project
ENV SHOP=/shop
RUN mkdir $SHOP
WORKDIR $SHOP
RUN mkdir /apk
COPY . .

RUN yum install -y dos2unix
RUN dos2unix /shop/entrypoint.sh

CMD bash entrypoint.sh