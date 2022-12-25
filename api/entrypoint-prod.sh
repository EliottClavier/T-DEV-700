sed -i 's/8080/'"$API_PORT"'/g' /usr/local/tomcat/conf/server.xml
bash catalina.sh run