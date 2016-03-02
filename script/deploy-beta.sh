#! /bin/sh
rm local.properties
./gradlew clean build uploadArchives
