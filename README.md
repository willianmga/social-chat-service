# Reactive Chat Server

Reactive WebSocket server which implements the chat domain and handles the messages sent by the front-end client. 

## FEATURES:
* Management of users and chat groups
* Management of Authentication and Sessions
* Receive and Broadcast of Chat messages to chat users

## TECHNOLOGIES
* Spring WebFlux and Spring Boot
* Jetty WebSocket Server
* MongoDB Database
* Docker and Docker compose
* Deployment to Heroku
* Java 8
* Maven

## LIVE TEST INSTANCE
* WebSocket: wss://reactive-chat-back.herokuapp.com/chat

## FRONT-END
* Live client: www.socialchat.live
* Repository: https://github.com/willianmga/reactive-chat-front

## TODO:
* Implement a encrypter/decryper to protect sensetive data such as passwords
* Group and Contact mapping to User so that a user talks only to whom he wants
* Move from Maven to Gradle