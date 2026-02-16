# Keycloak - Sunucuya Deploy

## Ön Gereksinimler

- Sunucu: Ubuntu/Debian
- Docker ve Docker Compose kurulu
- Nginx kurulu
- DNS: `auth.iyihislerapp.com` → sunucu IP (Cloudflare proxy açık)

---

## 0. Eski kurulumu temizle (önemli)

Keycloak'ı ayrı deploy etmeden önce eski monolitik kurulumu durdur:

```bash
# Sunucuya bağlan
ssh psiko@187.77.77.215

# Eski container'ları durdur
cd ~/PsikoHekimBackend
docker compose -f docker-compose/archive/docker-compose.prod.yml --env-file .env down 2>/dev/null || true

# Veya cleanup script (Keycloak projesinde)
cd ~/Keycloak
bash cleanup-server.sh
```

**Not:** `down` volume'ları silmez, veri kalır. Tam silmek için `down -v` (dikkatli kullan).

---

## 1. Projeyi sunucuya al

```bash
# Git ile (repo varsa)
git clone https://github.com/faruksrky/keycloak-auth-service.git Keycloak
cd Keycloak
git checkout PsikoHekim   # gerekirse

# Veya scp ile
# scp -r /Users/fs648/Desktop/Keycloak psiko@187.77.77.215:~/Keycloak
```

### 2. .env oluştur

```bash
cd ~/Keycloak
cp .env.production.example .env
nano .env   # POSTGRES_PASSWORD ve KEYCLOAK_ADMIN_PASSWORD doldur
```

### 3. Deploy

```bash
bash deploy-server.sh
```

### 4. Test

https://auth.iyihislerapp.com/admin/master/console/

---

## Manuel adımlar

```bash
# Nginx
sudo cp nginx/auth.iyihislerapp.com.cloudflare.conf /etc/nginx/sites-available/auth.iyihislerapp.com
sudo ln -sf /etc/nginx/sites-available/auth.iyihislerapp.com /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

# Docker
docker compose -f docker-compose/docker-compose.yml --env-file .env up -d
```

## Durdur

```bash
docker compose -f docker-compose/docker-compose.yml down
```

---

## Not: Aynı sunucuda api.iyihislerapp.com varsa

auth istekleri yanlışlıkla api'ye gidiyorsa:

```bash
cd ~/PsikoHekimBackend/nginx && sudo bash fix-auth-routing.sh
```
