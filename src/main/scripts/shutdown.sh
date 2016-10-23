#!/bin/bash

. "$( dirname "${BASH_SOURCE[0]}" )/setenv.sh"

if [ ! -f "$JSVC_PID_FILE" ]; then
	echo "dscache not running." >&2
	exit 1
fi

echo 'Stopping dscache...'

$JSVC_EXECUTABLE -stop -cp "$JAVA_CLASSPATH" -user "$JSVC_USER" \
	-pidfile $JSVC_PID_FILE $JAVA_OPTS $JAVA_MAIN_CLASS

sleep 10

