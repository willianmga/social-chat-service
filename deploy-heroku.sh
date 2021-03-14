#!/bin/bash

## Script for automating deployment of source code into Heroku

sh build-docker-image.sh

# Deploys docker image to Heroku

export USERNAME="willian.bodnariuc@gmail.com"

heroku login --username $USERNAME
export TOKEN=$(heroku auth:token)

echo
echo Using Heroku Auth token $TOKEN
echo

docker login --username=$USERNAME --password=$TOKEN registry.heroku.com
docker tag $APPNAME:latest registry.heroku.com/$APPNAME/web
docker push registry.heroku.com/$APPNAME/web

heroku container:release web --app $APPNAME

# Tag the release

sh tag-release.sh