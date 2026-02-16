# Cloudflare Full SSL Kurulumu

Cloudflare **Full** modu için sunucuda SSL sertifikası gerekir. Cloudflare Origin Certificate ücretsizdir.

## 1. Cloudflare'da Sertifika Oluştur

1. **Cloudflare Dashboard** → **iyihislerapp.com** → **SSL/TLS** → **Origin Server**
2. **Create Certificate** tıkla
3. Ayarlar:
   - **Private key type:** RSA (2048)
   - **Hostnames:** `auth.iyihislerapp.com` (veya `*.iyihislerapp.com` tüm subdomain'ler için)
   - **Certificate Validity:** 15 years
4. **Create** → **Origin Certificate** ve **Private Key** kopyala

## 2. Sunucuda Dosyaları Oluştur

```bash
sudo mkdir -p /etc/ssl/cloudflare
sudo nano /etc/ssl/cloudflare/auth.iyihislerapp.com.pem
```

**Origin Certificate** içeriğini yapıştır (-----BEGIN CERTIFICATE----- ile başlar):
```
-----BEGIN CERTIFICATE-----
...
-----END CERTIFICATE-----
```

```bash
sudo nano /etc/ssl/cloudflare/auth.iyihislerapp.com.key
```

**Private Key** içeriğini yapıştır (-----BEGIN PRIVATE KEY----- ile başlar):
```
-----BEGIN PRIVATE KEY-----
...
-----END PRIVATE KEY-----
```

```bash
sudo chmod 600 /etc/ssl/cloudflare/auth.iyihislerapp.com.key
```

## 3. Nginx SSL Config Uygula

```bash
cd ~/Keycloak
git pull origin PsikoHekim

# Eski config'i yedekle, SSL config'i uygula
sudo cp nginx/auth.iyihislerapp.com.ssl.conf /etc/nginx/sites-available/auth.iyihislerapp.com
sudo nginx -t && sudo systemctl reload nginx
```

## 4. Cloudflare SSL Modu

**SSL/TLS** → **Overview** → **Full** (veya **Full (strict)**)

## 5. Test

https://auth.iyihislerapp.com/admin/master/console/
