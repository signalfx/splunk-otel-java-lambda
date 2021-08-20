#!/bin/bash

if [[ ! "$#" -eq 1 ]]
then
  echo "usage: $0 NEXT_RELEASE_VERSION "
  exit 1
fi

cd github-clone

echo "Releasing binaries for $1"

mvn -B clean package --file ./wrapper/pom.xml
cp ./wrapper/target/otel-java-lambda-wrapper-$1.jar otel-java-lambda-wrapper.jar

echo "GitHub release" && gh release create "${CI_COMMIT_TAG}" otel-java-lambda-wrapper.jar --draft --title "Release $1" --notes "Draft"

sed -i 's/OSSRH_USER/${OSSRH_USER}/g' ./wrapper/release-settings.xml
sed -i 's/OSSRH_PASSWORD/${OSSRH_PASSWORD}/g' ./wrapper/release-settings.xml
cat <(echo -e "${OSSRH_GPG_SECRET_KEY}") | gpg --batch --import
gpg --list-secret-keys --keyid-format LONG
echo "Maven2 OSSRH release" && mvn -B -DperformRelease=true -Dgpg.passphrase=${OSSRH_GPG_SECRET_KEY_PASSWORD} deploy --file ./wrapper/pom.xml -s ./wrapper/release-settings.xml