#!/bin/bash

export VERSION=$1

if [ -z ${VERSION} ]; then
  echo "Please, specify the new version prior to releasing the current one"
  exit
fi

export SNAPSHOT="-SNAPSHOT"

export NEW_VERSION=$VERSION$SNAPSHOT

export CURRENT_VERSION=$(mvn -q \
-Dexec.executable=echo \
-Dexec.args='${project.version}' \
--non-recursive \
exec:exec)

export RELEASE_VERSION=${CURRENT_VERSION%$SNAPSHOT}

echo
echo "Release version is: ${RELEASE_VERSION}"
echo "New development version is: ${NEW_VERSION}"
echo

git checkout master
git pull origin master

git tag -a $RELEASE_VERSION -m "Release ${RELEASE_VERSION}"
git push origin ${RELEASE_VERSION}

mvn versions:set -DnewVersion=${NEW_VERSION}
mvn versions:commit

git add pom.xml
git commit -m "Opened version: ${NEW_VERSION}"
git push origin master