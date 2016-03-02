#! /bin/sh
rm local.properties
./gradlew clean build javadoc uploadArchives uploadJavadoc
