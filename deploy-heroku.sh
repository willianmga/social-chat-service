#!/bin/bash

## Script for automating deployment of source code into Heroku

export NEW_VERSION=$1
export APPNAME=reactive-chat-back

# Package the jar and build the docker image

sh build-docker-image.sh

# Deploys docker image to Heroku

export USERNAME="willian.bodnariuc@gmail.com"

heroku login --username $USERNAME
export TOKEN=$(heroku auth:token)

echo
echo Using Heroku Auth token $TOKEN
echo

docker login --username=$USERNAME --password=$TOKEN registry.heroku.com || { echo 'Failed to login to heroku. Exiting.' ; exit 1; }
docker tag $APPNAME:latest registry.heroku.com/$APPNAME/web
docker push registry.heroku.com/$APPNAME/web || { echo 'Failed to push to heroku. Exiting.' ; exit 1; }

heroku container:release web --app $APPNAME || { echo 'Failed to deploy to heroku. Exiting.' ; exit 1; }

# Tag the release

sh tag-release.sh ${NEW_VERSION}