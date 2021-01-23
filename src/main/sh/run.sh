#/bin/sh

FOLDER="../html/";

if [ $# -eq 1 ]; then
    FILE="$FOLDER/$1";
else
    FILE="$FOLDER/ants_worst.html";
fi

mvn -f ../../../pom.xml clean install
appletviewer -J-Djava.security.policy=../resources/policy.txt "$FILE"