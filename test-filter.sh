#!/bin/bash
# Filter'ın çalışıp çalışmadığını test etmek için basit bir script

echo "🔍 Keycloak servisine test isteği gönderiliyor..."
echo ""

# OPTIONS preflight request (CORS test)
echo "1. OPTIONS request (preflight):"
curl -X OPTIONS \
  -H "Origin: https://1b836a21.psikohekimfrontend.pages.dev" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v \
  https://keycloak.iyihislerapp.com/keycloak/getToken 2>&1 | grep -i "access-control\|origin\|HTTP"

echo ""
echo "2. Servis erişilebilir mi?"
curl -I https://keycloak.iyihislerapp.com/keycloak/getToken 2>&1 | head -5

