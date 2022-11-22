# On fixe la version de gradle à la 7.5.1, ce qui permet de ne pas utiliser gradlew 
# qui télécharge la bonne version de gradle à chaque build
FROM gradle:7.5.1-jdk17

RUN apt-get install bash

ENV HOME=/api
RUN mkdir $HOME
COPY . $HOME
WORKDIR $HOME

RUN gradle --no-daemon --warning-mode all --console=plain getDeps

EXPOSE 8080
CMD bash entrypoint.sh