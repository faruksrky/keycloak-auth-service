#!/bin/bash
# Keycloak - Sunucuya Deploy (VPS)
# Sunucuda: cd ~/Keycloak && bash deploy-server.sh
#
# Önce: .env oluştur (cp .env.production.example .env)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f .env ]; then
  echo "HATA: .env dosyası yok."
  echo "  cp .env.production.example .env"
  echo "  Sonra POSTGRES_PASSWORD ve KEYCLOAK_ADMIN_PASSWORD doldur."
  exit 1
fi

echo ">>> 1. Nginx config uygulanıyor..."
if [ -f nginx/auth.iyihislerapp.com.cloudflare.conf ]; then
  sudo cp nginx/auth.iyihislerapp.com.cloudflare.conf /etc/nginx/sites-available/auth.iyihislerapp.com
  sudo ln -sf /etc/nginx/sites-available/auth.iyihislerapp.com /etc/nginx/sites-enabled/auth.iyihislerapp.com
  sudo nginx -t && sudo systemctl reload nginx
  echo "    Nginx OK"
else
  echo "    UYARI: nginx/auth.iyihislerapp.com.cloudflare.conf bulunamadı"
fi

echo ""
echo ">>> 2. Keycloak + PostgreSQL başlatılıyor..."
docker compose -f docker-compose/docker-compose.yml --env-file .env up -d

echo ""
echo ">>> Tamamlandı."
echo ">>> Keycloak: https://auth.iyihislerapp.com"
echo ">>> Log: docker compose -f docker-compose/docker-compose.yml logs -f keycloak"
