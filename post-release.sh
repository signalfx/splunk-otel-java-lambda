#!/bin/bash

# -- update POM / examples to next development version
# -- update release properties
# -- make sure release settings are not modified
# -- commit
# -- push

if [[ ! "$#" -eq 2 ]]
then
  echo "usage: $0 NEXT_RELEASE_VERSION NEXT_DEVELOPMENT_VERSION"
  exit 1
fi

current_release=`cat release.properties | grep "current.release=" | cut -c17-`
next_dev=$2
next_release=$1

echo "Current release: $current_release"
echo "Next release: $next_release"
echo "Next development: $next_dev"

mvn versions:set -DnewVersion="$next_dev" -DgenerateBackupPoms=false  -f wrapper/pom.xml
sed -i -r "s/(wrapper:\s')$next_release/\1$next_dev/" ./examples/build.gradle

sed -i "s/$current_release/$next_release/" ./release.properties

git checkout HEAD -- wrapper/release-settings.xml

git commit -a -m "preparing next development $next_dev"