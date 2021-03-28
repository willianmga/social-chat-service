FROM openjdk:8-jdk-alpine

LABEL maintainer="willian.azevedo (willian-mga@hotmail.com)"

ADD target/reactive-chat-back.jar /opt/chat/reactive-chat-back.jar

WORKDIR /opt/chat

EXPOSE 8080

ENV PORT=8080
ENV CHAT_MONGO_SERVER=localhost:27017
ENV CHAT_MONGO_USERNAME=evitcaer
ENV CHAT_MONGO_PASSWORD=johnjones
ENV CHAT_MONGO_CONNECTION_STRING=mongodb://%s:%s@%s/%s
ENV CHAT_MONGO_DATABASE=socialchat
ENV CHAT_MONGO_AUTH_DATABASE=admin

CMD java -jar reactive-chat-back.jar