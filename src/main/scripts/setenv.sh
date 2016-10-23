#!/bin/bash

JSVC_EXECUTABLE="/usr/lib/bigtop-utils/jsvc"
JSVC_PID_FILE=/var/run/dscache/dscache.pid

if [ -z "$JSVC_USER" ]; then
	JSVC_USER="$USER"
fi

DIST_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )"
LIB_DIR="$DIST_DIR/lib"
CONF_DIR="$DIST_DIR/conf"

JAVA_EXEC="$( which java )"

JAVA_CLASSPATH="$CONF_DIR"
for lib in `ls $LIB_DIR`
do
    JAVA_CLASSPATH=$JAVA_CLASSPATH:"$LIB_DIR/$lib"
done

JAVA_MAIN_CLASS="com.maxent.dscache.DSCache"
JAVA_OPTS="-Ddistribution.dir=$DIST_DIR -Dfile.encoding=UTF-8 -Duser.timezone=UTC -Djava.awt.headless=true -Dcom.sun.management.jmxremote.port=9689 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

if [ -z "$JAVA_HOME" ]; then
	export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home
fi

export JSVC_EXECUTABLE JSVC_PID_FIL JSVC_USER DIST_DIR CONF_DIR JAVA_EXEC \
	JAVA_CLASSPATH JAVA_MAIN_CLASS JAVA_HOME
