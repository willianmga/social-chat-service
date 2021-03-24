#!/bin/bash

## Script for automating deployment of source code into Heroku

export DOCKER_HUB_REPO="willianmga"
export APPNAME="reactive-chat-back"

export DOCKER_HUB_USERNAME="willianmga"
export HEROKU_USERNAME="willian.bodnariuc@gmail.com"

export NEW_VERSION=$1
export CURRENT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
export SNAPSHOT="-SNAPSHOT"
export RELEASE_VERSION=${CURRENT_VERSION%$SNAPSHOT}

# Package the jar

mvn clean package

# Build, tag and push docker image

#docker login login --username=$DOCKER_HUB_USERNAME --password $DOCKER_HUB_TOKEN || { echo 'Failed to login to docker hub. Exiting.' ; exit 1; }

docker build -t $APPNAME:$RELEASE_VERSION -t $APPNAME:latest .

docker tag $APPNAME:$RELEASE_VERSION $DOCKER_HUB_REPO/$APPNAME:$RELEASE_VERSION
docker tag $APPNAME:latest $DOCKER_HUB_REPO/$APPNAME:latest

docker push $DOCKER_HUB_REPO/$APPNAME:$RELEASE_VERSION
docker push $DOCKER_HUB_REPO/$APPNAME:latest

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

sh tag-release.sh ${NEW_VERSION}