#/bin/sh

FOLDER="../html/";

FILE="$FOLDER/ants_easy.html";

mvn -f ../../../pom.xml clean install
appletviewer -J-Djava.security.policy=../resources/policy.txt "$FILE"