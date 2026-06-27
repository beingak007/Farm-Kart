#!/usr/bin/env bash
# Stop all Farm Kart screen sessions

SESSIONS="fk-marketplace fk-farmer fk-buyer fk-logistics fk-warehouse fk-market-price fk-admin fk-notification fk-catalog fk-reporting fk-ai-advisory fk-agent fk-ui"

for SESSION in $SESSIONS; do
    if screen -list | grep -q "$SESSION"; then
        screen -S "$SESSION" -X quit
        echo "Stopped [$SESSION]"
    else
        echo "[$SESSION] not running"
    fi
done
echo "All stopped."
