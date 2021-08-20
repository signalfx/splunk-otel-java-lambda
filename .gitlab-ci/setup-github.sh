#!/bin/bash

if [[ ! "$#" -eq 1 ]]
then
  echo "usage: $0 NEXT_RELEASE_VERSION "
  exit 1
fi

git config --global user.name releaser
git config --global user.email releaser@users.noreply.github.com

git clone https://splunk-o11y-gdi-bot:"${GITHUB_TOKEN}"@github.com/signalfx/splunk-otel-java-lambda.git github-clone
cd github-clone

echo "Preparing branch for release changes release-$1"
git checkout tags/${CI_COMMIT_TAG} -b "release-$1"