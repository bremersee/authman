#!/usr/bin/env bash
echo "Starting backend client ..."
java -cp bec.jar:lib/*.jar org.springframework.boot.loader.JarLauncher $@
