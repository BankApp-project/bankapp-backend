#!/usr/bin/env bash
# smoke-test.sh - Local smoke test for Docker Compose
# Usage: ./smoke-test.sh [HEALTH_URL]
set -euo pipefail
COMPOSE_FILE="docker-compose.yaml"
APP_SERVICE_NAME="bankapp"
HEALTH_URL="${1:-http://localhost:8080/actuator/health}"
RETRIES=30
SLEEP_INTERVAL=5
cleanup() {
  echo "\nTearing down Docker Compose environment..."
  docker compose -f "$COMPOSE_FILE" down --volumes --remove-orphans
}
trap cleanup EXIT
echo "🔍 Validating $COMPOSE_FILE..."
docker compose -f "$COMPOSE_FILE" config --quiet
echo "✅ Compose file is valid."
echo "🚀 Building and starting services..."
docker compose -f "$COMPOSE_FILE" up --build --detach
echo "✅ Services are up."
echo "⏳ Waiting for application to be healthy at $HEALTH_URL..."
for i in $(seq 1 $RETRIES); do
  if curl --silent --fail "$HEALTH_URL" > /dev/null; then
    echo "✅ Application is healthy!"
    exit 0
  else
    echo "  Waiting ($i/$RETRIES)..."
    sleep $SLEEP_INTERVAL
  fi
done
echo "❌ Application did not become healthy within $((RETRIES * SLEEP_INTERVAL)) seconds."
echo "----- Logs for $APP_SERVICE_NAME -----"
docker compose -f "$COMPOSE_FILE" logs $APP_SERVICE_NAME
exit 1
