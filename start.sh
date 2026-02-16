#!/bin/bash
# Keycloak - Başlat
# cd Keycloak && ./start.sh

set -e
cd "$(dirname "$0")"

echo ">>> Keycloak + PostgreSQL başlatılıyor..."
docker compose -f docker-compose/docker-compose.yml up -d

echo ""
echo ">>> Keycloak: http://localhost:8080"
echo ">>> Admin: KEYCLOAK_ADMIN / KEYCLOAK_ADMIN_PASSWORD (.env veya varsayılan: admin/admin)"
echo ""
echo ">>> Log: docker compose -f docker-compose/docker-compose.yml logs -f keycloak"
echo ">>> Durdur: docker compose -f docker-compose/docker-compose.yml down"
