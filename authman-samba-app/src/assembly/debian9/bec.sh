#!/usr/bin/env bash
JAVA="/usr/bin/java"
MAIN_JAR="/usr/share/java/bec.jar"
LIB_DIR="/var/lib/bec/lib"
#PID_FILE="/var/lib/bec/bec.pid"
source /etc/bec/env.conf
$JAVA $JAVA_OPTS -cp $MAIN_JAR:$LIB_DIR/*.jar org.springframework.boot.loader.JarLauncher $RUN_ARGS
# $@ > /dev/null 2>&1 & echo $! > $PID_FILE
