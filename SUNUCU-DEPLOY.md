# Keycloak Projesi - Sunucu Deploy Rehberi

## Özet

- **auth.iyihislerapp.com** → Nginx
  - `/keycloak/*`, `/users/*` → **keycloak-auth-service:6700** (Spring Boot, CORS destekli)
  - `/`, `/admin`, `/realms` → **Keycloak:8080** (Keycloak imajı)

## Sunucuda Keycloak Zaten Çalışıyorsa (srv1377590 gibi)

```bash
cd ~/Keycloak   # veya clone: git clone ... Keycloak && cd Keycloak
git pull origin PsikoHekim

# 1. Nginx config uygula
sudo cp nginx/auth.iyihislerapp.com.cloudflare.conf /etc/nginx/sites-available/auth.iyihislerapp.com
sudo ln -sf /etc/nginx/sites-available/auth.iyihislerapp.com /etc/nginx/sites-enabled/auth.iyihislerapp.com
sudo nginx -t && sudo systemctl reload nginx

# 2. keycloak-auth-service başlat (Keycloak zaten çalıştığı için standalone mod)
bash deploy-server.sh
```

`deploy-server.sh` Keycloak container'ını görürse otomatik olarak sadece **keycloak-auth-service** deploy eder.

## Sıfırdan Deploy (Keycloak yok)

```bash
cd ~/Keycloak
cp .env.production.example .env
# .env içinde POSTGRES_PASSWORD ve KEYCLOAK_ADMIN_PASSWORD doldur

bash deploy-server.sh
```

Bu durumda postgres + keycloak + keycloak-auth-service tam stack başlar.

## Doğrulama

```bash
# keycloak-auth-service çalışıyor mu?
curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:6700/keycloak/getToken
# 200 veya 401 beklenir (401 = endpoint var, auth gerekli)

# Nginx üzerinden
curl -s -o /dev/null -w "%{http_code}" https://auth.iyihislerapp.com/keycloak/getToken
```

## Loglar

```bash
docker logs keycloak-auth-service -f
```

## Sorun Giderme

| Sorun | Çözüm |
|-------|-------|
| CORS hatası | keycloak-auth-service 6700'de çalışıyor olmalı. Nginx `/keycloak/` ve `/users` → 6700'e proxy etmeli. |
| 502 Bad Gateway | keycloak-auth-service container'ı çalışmıyor. `docker ps` ile kontrol et. |
| Port 6700 kullanımda | Başka bir servis 6700 kullanıyor olabilir. `lsof -i :6700` ile kontrol et. |
