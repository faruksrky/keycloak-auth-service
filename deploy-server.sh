#!/bin/bash
# Keycloak - Sunucuya Deploy (VPS)
# Sunucuda: cd ~/Keycloak && bash deploy-server.sh
#
# Önce: .env oluştur (cp .env.production.example .env)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Keycloak zaten çalışıyorsa .env gerekmez (standalone mod)
NEED_ENV=1
if docker ps --format '{{.Names}}' | grep -q '^keycloak$'; then
  NEED_ENV=0
fi
if [ "$NEED_ENV" = "1" ] && [ ! -f .env ]; then
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
echo ">>> 2. keycloak-auth-service başlatılıyor..."
# Keycloak zaten çalışıyorsa (srv1377590 gibi) sadece keycloak-auth-service
if docker ps --format '{{.Names}}' | grep -q '^keycloak$'; then
  echo "    Keycloak zaten çalışıyor, sadece keycloak-auth-service deploy ediliyor..."
  docker compose -f docker-compose/docker-compose.standalone.yml up -d --build
else
  echo "    Tam stack (postgres + keycloak + keycloak-auth-service) başlatılıyor..."
  docker compose -f docker-compose/docker-compose.yml --env-file .env up -d --build
fi

echo ""
echo ">>> Tamamlandı."
echo ">>> Auth (login, users): https://auth.iyihislerapp.com/keycloak/getToken"
echo ">>> Keycloak Admin: https://auth.iyihislerapp.com/admin"
echo ">>> Log keycloak-auth-service: docker logs keycloak-auth-service -f"
