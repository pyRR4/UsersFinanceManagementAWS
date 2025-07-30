#!/bin/bash
set -e

echo "Starting Liquibase migration..."

liquibase \
  --url="${JDBC_URL}" \
  --username="${DB_USER}" \
  --password="${DB_PASS}" \
  --changeLogFile=changelogs/db.changelog-master.xml \
  update

echo "Migration complete!"