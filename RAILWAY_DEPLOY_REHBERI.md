# 🚀 Keycloak Auth Service - Railway Deploy Rehberi

## 📋 Proje Bilgileri
- **Proje Adı:** Keycloak Auth Service
- **Port:** 6700
- **Tip:** Spring Boot (Java 21)
- **Build:** Maven
- **Dizin:** `/Users/fs648/Desktop/Keycloak`

---

## 🚀 Deploy Adımları

### 1️⃣ GitHub Repo Hazırlığı

```bash
cd /Users/fs648/Desktop/Keycloak

# Git repo oluştur (eğer yoksa)
git init

# .gitignore kontrol et
# Eğer yoksa oluştur:
cat > .gitignore << EOF
target/
*.log
.idea/
*.iml
.mvn/
mvnw
mvnw.cmd
EOF

# GitHub'da repo oluşturun: https://github.com/new
# Repo adı: keycloak-auth-service

# Remote ekleyin (YOUR_USERNAME'i değiştirin)
git remote add origin https://github.com/YOUR_USERNAME/keycloak-auth-service.git

# Commit ve push
git add .
git commit -m "Initial commit - Railway deploy ready"
git push -u origin main
```

### 2️⃣ Railway'a Kayıt Olun

1. https://railway.app → **"Start a New Project"**
2. **"Login with GitHub"** → GitHub hesabınızla giriş yapın

### 3️⃣ Yeni Proje Oluşturun

1. Railway Dashboard → **"New Project"**
2. **"Deploy from GitHub repo"**
3. `keycloak-auth-service` repo'sunu seçin
4. Railway otomatik olarak Java projesini algılayacak

### 4️⃣ Environment Variables Ekleyin

Railway → Your Service → **Variables** sekmesi:

```bash
# Keycloak Admin Client
APP_KEYCLOAK_ADMIN_CLIENT_ID=PH
APP_KEYCLOAK_ADMIN_CLIENT_SECRET=rTuXY9QyjACS2heH2OGgy19tKEZrzlJw

# Keycloak Admin CLI
APP_KEYCLOAK_ADMIN_CLI_CLIENT_ID=admin-cli
APP_KEYCLOAK_ADMIN_CLI_CLIENT_SECRET=5NZHfNmz78pV6WcKuJUjDDlzsJILEBef

# Keycloak Realm
APP_KEYCLOAK_REALM=PsikoHekim

# Keycloak Server URL
# NOT: Keycloak server'ınızın URL'ini buraya yazın
APP_KEYCLOAK_SERVER_URL=http://localhost:8080
# Eğer Keycloak server da deploy edecekseniz:
# APP_KEYCLOAK_SERVER_URL=https://keycloak-server.railway.app
```

**application.yml'deki format:**
```yaml
app:
  keycloak:
    admin:
      clientId: ${APP_KEYCLOAK_ADMIN_CLIENT_ID:PH}
      clientSecret: ${APP_KEYCLOAK_ADMIN_CLIENT_SECRET}
    adminClientId: ${APP_KEYCLOAK_ADMIN_CLI_CLIENT_ID:admin-cli}
    adminClientSecret: ${APP_KEYCLOAK_ADMIN_CLI_CLIENT_SECRET}
    realm: ${APP_KEYCLOAK_REALM:PsikoHekim}
    serverUrl: ${APP_KEYCLOAK_SERVER_URL:http://localhost:8080}
```

### 5️⃣ Port Ayarı (Zaten Hazır)

`application.yml`'de port ayarı zaten yapılmış:
```yaml
server:
  port: ${PORT:6700}  # Railway PORT env var'ını kullan
```

### 6️⃣ Deploy URL'ini Alın

1. Railway → Your Service → **Settings** → **Networking**
2. **"Generate Domain"** butonuna tıklayın
3. URL: `https://keycloak-auth-service.up.railway.app` (örnek)

### 7️⃣ Custom Domain (Opsiyonel)

**Eğer `keycloak.iyihislerapp.com` kullanmak istiyorsanız:**

1. Railway → Settings → Networking → **Custom Domain**
2. **"Add Domain"** → `keycloak.iyihislerapp.com`
3. Railway DNS kayıtlarını gösterir
4. Cloudflare DNS'e ekleyin:
   - **Type:** `CNAME`
   - **Name:** `keycloak`
   - **Target:** Railway'ın verdiği CNAME değeri
   - **Proxy:** ✅ Proxied

---

## 🔄 Deployment Sonrası

### 1. CORS Pattern'lerini Güncelleyin

`CustomCorsFilter.java` dosyasını güncelleyin:

```java
private static final List<String> ALLOWED_ORIGIN_PATTERNS = Arrays.asList(
    "https://.*\\.psikohekimfrontend\\.pages\\.dev",
    "https://psikohekimfrontend\\.pages\\.dev",
    "https://.*\\.iyihislerapp\\.com",
    "https://iyihislerapp\\.com",
    "https://.*\\.up\\.railway\\.app",  // YENİ: Railway URL pattern
    "http://localhost:.*"
);
```

**Değişiklikleri push edin:**
```bash
git add .
git commit -m "Add Railway URL to CORS patterns"
git push
```

Railway otomatik olarak yeniden deploy edecek.

### 2. Cloudflare Pages Environment Variables Güncelleme

**Cloudflare Pages → Settings → Environment Variables:**

```bash
# Yeni Railway URL
VITE_KEYCLOAK_BASE_URL=https://keycloak-auth-service.up.railway.app
# VEYA custom domain kullanıyorsanız:
VITE_KEYCLOAK_BASE_URL=https://keycloak.iyihislerapp.com

# Endpoint'ler
VITE_KEYCLOAK_GET_TOKEN_URL=${VITE_KEYCLOAK_BASE_URL}/keycloak/getToken
VITE_KEYCLOAK_GET_USER_INFO_URL=${VITE_KEYCLOAK_BASE_URL}/keycloak/userInfo
VITE_KEYCLOAK_USERS_URL=${VITE_KEYCLOAK_BASE_URL}/users
```

### 3. Frontend'i Redeploy Edin

Cloudflare Pages → Deployments → **Retry deployment**

---

## 🧪 Test Etme

### Health Check
```bash
curl https://keycloak-auth-service.up.railway.app/actuator/health
```

### CORS Test
Browser console'da:
```javascript
fetch('https://keycloak-auth-service.up.railway.app/keycloak/getToken', {
  method: 'OPTIONS',
  headers: { 'Origin': 'https://1b836a21.psikohekimfrontend.pages.dev' }
}).then(r => console.log('Status:', r.status));
```

---

## ✅ Checklist

- [ ] GitHub repo oluşturuldu
- [ ] Railway'a kayıt olundu
- [ ] Proje oluşturuldu ve repo bağlandı
- [ ] Environment variables eklendi
- [ ] Deploy URL alındı
- [ ] CORS pattern'leri güncellendi
- [ ] Cloudflare Pages environment variables güncellendi
- [ ] Frontend redeploy edildi
- [ ] Test edildi ✅
