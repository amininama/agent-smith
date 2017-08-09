#!/bin/bash

#properties======
TOTAL_CLIENT_COUNT=20
CONCURRENT_CLIENT_COUNT=5
SLEEP_BETWEEN_REQUESTS=100
AGENTS_FILE=./agents
TARGETS_FILE=./targets
USE_MASTER_PROXY="true"
SOCKS_PROXY_HOST=127.0.0.1
SOCKS_PROXY_PORT=1234
MYSQL_ROOT_PASSWORD=chizchizchiz
RELOAD_PROXIES_ON_BOOT=true
BUILD_BEFOREHAND="false"
CLIENT_TIMEOUT_SECS=3
#================

if [ "$BUILD_BEFOREHAND" == "true" ]; then
    cd ..
    echo "---------- building the-smith -----------"
    mvn clean install -DskipTests  || { echo "Maven failed to build the-smith!"; echo "Deployment cancelled!"; exit 1; }
    echo "------ Maven build successful --------"
    cd ./bin
fi
mysql -uroot -p$MYSQL_ROOT_PASSWORD -e"CREATE DATABASE IF NOT EXISTS agent_smith"

PROJECT_HOME='../target'
STARTUP_LOG='./startup.log'
MEM_MIN='128M'
MEM_MAX='1024M'
OPTS='-server -XX:+AggressiveOpts -Djava.net.preferIPv4Stack=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=5400 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dspring.profiles.active=production'
if [ "$USE_MASTER_PROXY" == "true" ]; then
    OPTS=$OPTS' -DsocksProxyHost='$SOCKS_PROXY_HOST' -DsocksProxyPort='$SOCKS_PROXY_PORT
fi
GC_OPTS='-XX:+UseParallelOldGC -XX:ParallelGCThreads=2'
JAR=$(JARS=($PROJECT_HOME/*.jar); IFS=:; echo "${JARS[*]}")
APP_OPTS='--thread.count='$TOTAL_CLIENT_COUNT' --sleep.time.millis='$SLEEP_BETWEEN_REQUESTS' --agents.source='$AGENTS_FILE
APP_OPTS=$APP_OPTS' --targets.source='$TARGETS_FILE' --spring.datasource.password='$MYSQL_ROOT_PASSWORD
APP_OPTS=$APP_OPTS' --proxies.load.on.boot='$RELOAD_PROXIES_ON_BOOT' --thread.concurrent='$CONCURRENT_CLIENT_COUNT
APP_OPTS=$APP_OPTS' --client.timeout.secs='$CLIENT_TIMEOUT_SECS
export LC_ALL='fa_IR.utf8'
java $OPTS -Xms$MEM_MIN -Xmx$MEM_MAX $GC_OPTS -jar $JAR $APP_OPTS
