# Keycloak Realm Import

Bu klasör `psikohekim` realm'ini Keycloak başlangıcında otomatik import eder.

## İçerik

- **psikohekim-realm.json**: Realm, client, roller ve admin kullanıcı
  - Realm: `psikohekim`
  - Client: `psikohekim-frontend`
  - Roller: `admin`, `user`
  - Admin kullanıcı: `psikohekimofis@gmail.com` (şifre: `ChangeMe123!` - ilk girişte değiştir)

## Kullanım

```bash
cd /Users/fs648/Desktop/Keycloak
docker compose -f docker-compose/docker-compose.yml up -d
```
