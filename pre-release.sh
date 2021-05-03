#!/bin/bash

# -- update POM / examples / docs version to requested release
# -- commit
# -- tag release (v{VERSION})

if [[ ! "$#" -eq 1 ]]
then
  echo "usage: $0 NEXT_RELEASE_VERSION "
  exit 1
fi

current_dev=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f wrapper/pom.xml`
current_release=`cat release.properties | grep "current.release=" | cut -c17-`
next_release=$1

echo "Current release: $current_release"
echo "Current development: $current_dev"
echo "Next release: $next_release"

mvn versions:set -DnewVersion="$next_release" -DgenerateBackupPoms=false -f wrapper/pom.xml
sed -i "s/$current_release/$next_release/" ./README.md
sed -i "s/$current_release/$next_release/" ./outbound-context-propagation.md
sed -i -r "s/(wrapper:\s')$current_dev/\1$next_release/" ./examples/build.gradle

git commit -a -m "preparing release $next_release"
git tag -a "v$next_release" -m "release $next_release"