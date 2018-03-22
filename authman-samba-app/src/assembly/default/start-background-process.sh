#!/usr/bin/env bash
echo "Starting backend client ..."
java -cp bec.jar:lib/*.jar org.springframework.boot.loader.JarLauncher $@ > /dev/null 2>&1 & echo $! > bec.pid
echo "You can stop the process by calling  >>  stop-background-process.sh  <<"
