#!/bin/bash

sed -i 's/8080/'"$API_PORT"'/g' /usr/local/tomcat/conf/server.xml
catalina.sh run