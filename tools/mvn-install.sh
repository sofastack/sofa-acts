#!/bin/sh

cd .. && mvn install -DskipTests=false -Dmaven.test.skip=false -Djacoco.skip=false -DtestPhrase=install