 #!/bin/sh

# generate archetype
mvn clean install archetype:create-from-project -Dserver.port=8081

cd target/generated-sources/archetype

# cleanup archetype
JAR_NAME=springboot-rest-archetype-1.0
JAR_PATH=target/$JAR_NAME.jar
zip -d $JAR_PATH archetype-resources/*.iml
zip -d $JAR_PATH archetype-resources/*.sh
zip -d $JAR_PATH archetype-resources/log/*
zip -d $JAR_PATH archetype-resources/.idea/*

# add .gitignore
mkdir archetype-resources
cp ../../../.gitignore archetype-resources/.
zip -g $JAR_PATH archetype-resources/.gitignore

mvn install

cd ../../..
