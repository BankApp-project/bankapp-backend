#!/bin/sh
set -e
if [ -z "$RESEND_API_KEY" ]; then
  echo "ERROR: RESEND_API_KEY must be provided" >&2
  exit 1
fi
exec java -jar /app/app.jar --spring.profiles.active=dev
