#!/bin/bash
# Sunucuda eski PsikoHekim kurulumunu durdur (Keycloak ayrı deploy edeceğiz)
# DİKKAT: Veriler silinmez (volumes kalır). Tam silmek için: docker compose down -v
#
# Sunucuda: bash cleanup-server.sh

echo ">>> Eski container'lar durduruluyor..."

# PsikoHekimBackend monolitik compose varsa
if [ -d ~/PsikoHekimBackend ]; then
  cd ~/PsikoHekimBackend
  if [ -f docker-compose/archive/docker-compose.prod.yml ]; then
    docker compose -f docker-compose/archive/docker-compose.prod.yml --env-file .env down 2>/dev/null || true
    echo "    PsikoHekimBackend (archive) durduruldu"
  elif [ -f docker-compose/docker-compose.prod.yml ]; then
    docker compose -f docker-compose/docker-compose.prod.yml --env-file .env down 2>/dev/null || true
    echo "    PsikoHekimBackend durduruldu"
  fi
  cd -
fi

# Eski Keycloak container (farklı isimle çalışıyorsa)
docker stop psikohekim-keycloak 2>/dev/null || true
docker stop psikohekim-postgres 2>/dev/null || true
docker stop psikohekim-backend 2>/dev/null || true
docker stop psikohekim-redis 2>/dev/null || true

echo ""
echo ">>> Çalışan container'lar:"
docker ps -a --format "table {{.Names}}\t{{.Status}}" | grep -E "psiko|keycloak" || echo "  (yok)"

echo ""
echo ">>> Volume'lar silinmedi (veri korundu). Tam temizlik için:"
echo "   docker volume ls"
echo "   docker volume rm psikohekim_pgdata  # DİKKAT: Tüm DB silinir!"
