FROM openjdk:8-jdk-alpine

LABEL maintainer="willian.azevedo (willian-mga@hotmail.com)"

ADD target/reactive-chat-back.jar /opt/chat/reactive-chat-back.jar

WORKDIR /opt/chat

EXPOSE 8080

CMD java -Djava.security.egd=file:/dev/./urandom -jar reactive-chat-back.jar