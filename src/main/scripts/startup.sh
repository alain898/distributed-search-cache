#!/bin/bash

. "$( dirname "${BASH_SOURCE[0]}" )/setenv.sh"

if [ -f "$JSVC_PID_FILE" ]; then
	echo "dscache is already running. ($( cat "$JSVC_PID_FILE" ))" >&2
	exit 1
fi

echo "Starting dscache in Background. classpath : $JAVA_CLASSPATH"

$JSVC_EXECUTABLE -cp "$JAVA_CLASSPATH" -user "$JSVC_USER" \
	-pidfile $JSVC_PID_FILE $JAVA_OPTS $JAVA_MAIN_CLASS

sleep 5

