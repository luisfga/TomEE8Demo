#Dockerfile AINDA NÃO FUNCIONA no Heroku... é preciso setar a porta do tomcat no runtime. Provavelmente o jeito será estartar o tomee programaticamente, sem usar o plugin. Pesquisa em andamento...
FROM openjdk:11

MAINTAINER luisfga@gmail.com

RUN mkdir /app

COPY target/luisfga-tomee-demo-exec.jar /app/luisfga-tomee-demo-exec.jar

# O sistema precisa das seguintes variáveis de ambiente: APP_MAIL_SESSION_HOST, APP_MAIL_SESSION_PORT, APP_MAIL_SESSION_USERNAME e APP_MAIL_SESSION_PASSWORD

# Elas devem estar no sistema que buildar o container e devem ser passadas ao container adicionando flags '--build-arg' ao comando 'build':
# --build-arg APP_MAIL_SESSION_HOST --build-arg APP_MAIL_SESSION_PORT --build-arg APP_MAIL_SESSION_USERNAME --build-arg APP_MAIL_SESSION_PASSWORD

# Exemplo de como poderá ser o comando 'build':
# -> docker build -t luisfga-tomee-demo:dev . --build-arg APP_MAIL_SESSION_HOST --build-arg APP_MAIL_SESSION_PORT --build-arg APP_MAIL_SESSION_USERNAME --build-arg APP_MAIL_SESSION_PASSWORD

# OBS: essas variáveis podem ficar vazias e não serem informadas apenas se o sistema onde for feito o deploy ou release
# já tiver essas variáveis, por exemplo no Heroku, se elas estiverem configuradas como configVars

# Aqui os argumentos passados no comando 'build' com a flag '--build-arg' são consumidos (armazenados) durante a build
ARG APP_MAIL_SESSION_HOST
ARG APP_MAIL_SESSION_PORT
ARG APP_MAIL_SESSION_USERNAME
ARG APP_MAIL_SESSION_PASSWORD

# Agora, as variáveis consumidas nas linhas anteriores com o comando 'ARG' são colocadas no container mesmo
ENV APP_MAIL_SESSION_HOST=$APP_MAIL_SESSION_HOST
ENV APP_MAIL_SESSION_PORT=$APP_MAIL_SESSION_PORT
ENV APP_MAIL_SESSION_USERNAME=$APP_MAIL_SESSION_USERNAME
ENV APP_MAIL_SESSION_PASSWORD=$APP_MAIL_SESSION_PASSWORD

#Development port and entry point
#EXPOSE 8080
#ENTRYPOINT [ "java", "-jar", "-Dserver.port=8080", "/app/luisfga-tomee-demo-exec.jar"]

#Production port and entry point
#Heroku usa uma porta aleatória e a 'exporta' como PORT.
EXPOSE $PORT

ENTRYPOINT [ "java", "-jar", "/app/luisfga-tomee-demo-exec.jar", "run"]
#ENTRYPOINT [ "java", "-Xss512k -XX:MaxRAM=500m", "-jar", "-Dserver.port=$PORT", "/app/luisfga-tomee-demo-exec.jar"]

#TIPS
# não esquecer de -> mvn clean package

#DOCKER TIPS
# Excluir 'dangling' containers -> docker rmi $(docker images --filter "dangling=true" -q)

# Build command DEV  -> docker build -t luisfga-tomee-demo:dev .
# Build command PROD -> docker build -t luisfga-tomee-demo:prd .

# Run command (DEV) -> docker run -p 8080:8080 luisfga-tomee-demo:dev

# Listar containeres em execução -> docker ps

# Acessar bash dentro de um container que esteja rodando -> docker exec -it <containerID> /bin/bash

# inspecionar o exit code de um container que não estartou
# a qualquer momento -> docker inspect <container-id> --format='{{.State.ExitCode}}'
# logo após a falha -> echo $?

#HEROKU TIPS
#https://devcenter.heroku.com/articles/container-registry-and-runtime#building-and-pushing-image-s

#para buildar, fazer o push e depois release
#build + push -> heroku container:push web -a luisfga-tomee-demo
#release -> heroku container:release web -a luisfga-tomee-demo

# para fazer o push de uma imagem existente
# 1. é preciso marcar o container pra upload
#-> docker tag <image> registry.heroku.com/<app>/<process-type>
#por exemplo -> docker tag luisfga-tomee-demo:prd registry.heroku.com/luisfga-tomee-demo/web
# 2. em seguida, push da imagem 'tagueada'
#-> docker push registry.heroku.com/<app>/<process-type>
#por exemplo -> docker push registry.heroku.com/luisfga-tomee-demo/web
# 3. depois, release normal -> heroku container:release web -a luisfga-tomee-demo