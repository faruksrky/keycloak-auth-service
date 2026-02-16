# Keycloak Docker Compose

PostgreSQL + Keycloak (realm import ile).

## Başlat

```bash
cd /Users/fs648/Desktop/Keycloak
./start.sh
```

Veya:

```bash
cd /Users/fs648/Desktop/Keycloak
docker compose -f docker-compose/docker-compose.yml up -d
```

## Erişim

- **Keycloak:** http://localhost:8080
- **Admin Console:** http://localhost:8080/admin
- **Varsayılan:** admin / admin

## .env (opsiyonel)

```bash
cp .env.example .env
# Şifreleri düzenle
```

## Durdur

```bash
docker compose -f docker-compose/docker-compose.yml down
```
