#!/usr/bin/env bash
# Stop all Farm Kart screen sessions

SESSIONS="fk-app fk-notification fk-agent fk-ui"

for SESSION in $SESSIONS; do
    if screen -list | grep -q "$SESSION"; then
        screen -S "$SESSION" -X quit
        echo "Stopped [$SESSION]"
    else
        echo "[$SESSION] not running"
    fi
done
echo "All stopped."
