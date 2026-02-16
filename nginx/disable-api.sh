#!/bin/bash
# Api config'ini kaldır - sadece Keycloak çalışıyor
# PsikoHekim deploy edilince api config tekrar eklenir

set -e

echo ">>> Api nginx config devre dışı bırakılıyor..."
sudo rm -f /etc/nginx/sites-enabled/api.iyihislerapp.com
sudo rm -f /etc/nginx/sites-enabled/api.iyihislerapp.com.conf

echo ""
echo ">>> Aktif site config'leri:"
ls -la /etc/nginx/sites-enabled/

echo ""
echo ">>> Nginx test ve reload..."
sudo nginx -t && sudo systemctl reload nginx

echo ""
echo ">>> Tamamlandı. Sadece auth.iyihislerapp.com aktif."
