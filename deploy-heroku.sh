#!/bin/bash

## Script for automating deployment of source code into Heroku

export DOCKER_HUB_REPO="willianmga"
export APPNAME="social-chat-service"

export DOCKER_HUB_USERNAME="willianmga"
export HEROKU_USERNAME="willian.bodnariuc@gmail.com"

export NEW_VERSION=$1
export CURRENT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
export SNAPSHOT="-SNAPSHOT"
export RELEASE_VERSION=${CURRENT_VERSION%$SNAPSHOT}

# Deploys docker image to Heroku

heroku login --username $HEROKU_USERNAME
export TOKEN=$(heroku auth:token)

echo
echo Using Heroku Auth token $TOKEN
echo

docker login --username=$HEROKU_USERNAME --password=$TOKEN registry.heroku.com || { echo 'Failed to login to heroku. Exiting.' ; exit 1; }
docker tag $APPNAME:latest registry.heroku.com/$APPNAME/web
docker push registry.heroku.com/$APPNAME/web || { echo 'Failed to push to heroku. Exiting.' ; exit 1; }

heroku container:release web --app $APPNAME || { echo 'Failed to deploy to heroku. Exiting.' ; exit 1; }

# Tag the release

#sh tag-release.sh ${NEW_VERSION}