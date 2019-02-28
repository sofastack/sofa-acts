#!/bin/sh

cd .. && mvn test -DskipTests=false -Dmaven.test.skip=false -Djacoco.skip=false -DtestPhrase=install