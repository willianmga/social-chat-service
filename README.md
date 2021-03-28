# Social Chat Service

Reactive WebSocket server which implements the chat domain and handles the messages sent by the front-end client. 

## Links
* Chat: www.socialchat.live
* Live instance: wss://www.socialchat.live/ws
* Docker image registry: https://hub.docker.com/r/willianmga/social-chat-service

## Related Repos
* Front-End: https://github.com/willianmga/social-chat-front
* Authentication Server: https://github.com/willianmga/social-chat-auth-service

## Features:
* Management of users and chat groups
* Management of Authentication and Sessions
* Receive and Broadcast of Chat messages to chat users

## Technologies
* Spring WebFlux and Spring Boot
* Jetty WebSocket Server
* MongoDB Database
* Docker and Docker compose
* Deployment to AWS and Heroku
* Java 8
* Maven

## Environment Variables Accepted
* CHAT_MONGO_SERVER
* CHAT_MONGO_USERNAME
* CHAT_MONGO_PASSWORD
* CHAT_MONGO_AUTH_DATABASE
* CHAT_MONGO_DATABASE
* CHAT_MONGO_CONNECTION_STRING

## Todo:
* Implement a encrypter/decryper to protect sensetive data such as passwords
* Group and Contact mapping to User so that a user talks only to whom he wants
* Move from Maven to Gradle
