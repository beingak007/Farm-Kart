#!/usr/bin/env bash
# Creates all Farm Kart domain topics (mirrors FkTopics.java).
set -euo pipefail

BOOTSTRAP="${KAFKA_BOOTSTRAP_SERVERS:-kafka:29092}"
PARTITIONS="${KAFKA_TOPIC_PARTITIONS:-3}"
REPLICATION="${KAFKA_TOPIC_REPLICATION:-1}"

TOPICS=(
  farmkart.user.registered
  farmkart.user.role.changed
  farmkart.farmer.created
  farmkart.farmer.verified
  farmkart.farm.updated
  farmkart.buyer.created
  farmkart.crop.listed
  farmkart.crop.updated
  farmkart.crop.delisted
  farmkart.order.created
  farmkart.order.confirmed
  farmkart.order.cancelled
  farmkart.order.delivered
  farmkart.payment.initiated
  farmkart.payment.success
  farmkart.payment.failed
  farmkart.payment.settlement.completed
  farmkart.shipment.created
  farmkart.shipment.picked_up
  farmkart.shipment.delivered
  farmkart.warehouse.booked
  farmkart.warehouse.released
  farmkart.market.price.updated
  farmkart.sheet.uploaded
  farmkart.media.uploaded
  farmkart.insurance.requested
  farmkart.loan.requested
  farmkart.notification.triggered
  farmkart.audit.log.created
  farmkart.agent.task.created
  farmkart.agent.task.completed
)

echo "Bootstrapping ${#TOPICS[@]} topics on ${BOOTSTRAP} …"

for topic in "${TOPICS[@]}"; do
  /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server "${BOOTSTRAP}" \
    --create \
    --if-not-exists \
    --topic "${topic}" \
    --partitions "${PARTITIONS}" \
    --replication-factor "${REPLICATION}" \
    --config retention.ms=604800000 \
    --config cleanup.policy=delete
  echo "  ✓ ${topic}"
done

echo "Topic bootstrap complete."
