#!/bin/bash

export APPNAME=reactive-chat-back
mvn clean package
docker build -t $APPNAME:latest .