#!/bin/bash

# -- update POM / examples to next development version
# -- update release properties
# -- make sure release settings are not modified
# -- commit
# -- push

if [[ ! "$#" -eq 2 ]]
then
  echo "usage: $0 CURRENT_RELEASE_VERSION CURRENT_DEVELOPMENT_VERSION"
  exit 1
fi

cd github-clone
echo "Post release $1"

# at this point "current.release" from properties is previous release in fact
previous_release=`cat .gitlab-ci/release.properties | grep "current.release=" | cut -c17-`
current_dev=$2
current_release=$1

echo "Previous release: $previous_release"
echo "Current release: $current_release"
echo "Current development: $current_dev"

mvn versions:set -DnewVersion="$current_dev" -DgenerateBackupPoms=false  -f wrapper/pom.xml
sed -i -r "s/(wrapper:\s')$current_release/\1$current_dev/" ./examples/build.gradle

sed -i "s/$previous_release/$current_release/" .gitlab-ci/release.properties

# clean up
git checkout HEAD -- wrapper/release-settings.xml
rm otel-java-lambda-wrapper.jar

git commit -a -m "preparing next development cycle: $current_dev"

# push PR branch
echo "Pushing release branch" && git push -u origin "release-$current_release"

# open PR
echo "Creating PR" && gh pr create --title "Release ${VERSION} changeset" --body "See commits" --base main --head release-$current_release