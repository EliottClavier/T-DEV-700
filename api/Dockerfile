# On fixe la version de gradle à la 7.5.1, ce qui permet de ne pas utiliser gradlew 
# qui télécharge la bonne version de gradle à chaque build
FROM gradle:7.5.1-jdk17

RUN apt-get update && apt-get install -y bash dos2unix

ENV HOME=/api
RUN mkdir $HOME
COPY . $HOME
WORKDIR $HOME

RUN gradle --no-daemon --warning-mode all --console=plain getDeps

RUN dos2unix entrypoint.sh
CMD bash entrypoint.sh