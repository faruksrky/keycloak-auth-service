# 🔄 Backend Rebuild ve Tunnel Kurulum Adımları

## ✅ 1. Değişiklikler Temizlendi
- Gereksiz log'lama kodları kaldırıldı
- CustomCorsFilter temizlendi
- WebConfig temizlendi

## 🔨 2. Backend Rebuild

### IntelliJ IDEA:
1. **Maven > Reload Project** (sağ tık → Maven → Reload project)
2. **Build > Rebuild Project** (Cmd+Shift+F9 / Ctrl+Shift+F9)
3. **Run > Run 'KeycloakAuthServiceApplication'** veya Stop → Run

### Terminal:
```bash
cd /Users/fs648/Desktop/Keycloak
mvn clean compile
mvn spring-boot:run
```

## 🚇 3. Tunnel Kurulumu

### Adım 1: Tunnel'ı Başlat
```bash
cd /Users/fs648/Desktop/PsikoHekim/PsikoHekimFrontend
./start-tunnels.sh
```

Bu script:
- Keycloak için port 6700'den tunnel açar
- Yeni URL'leri `tunnel-urls.txt` dosyasına kaydeder
- URL'leri ekrana yazdırır

### Adım 2: Yeni URL'leri Not Et
Tunnel başladıktan sonra şu çıktıyı göreceksiniz:
```
2️⃣  KEYCLOAK (6700):
   https://[random-hash].trycloudflare.com
```

**VEYA** eğer `keycloak.iyihislerapp.com` kullanıyorsanız, bu URL zaten hazır.

### Adım 3: Tunnel Durumunu Kontrol Et
```bash
./check-tunnel.sh
```

## 🌐 4. Frontend Environment Variables Güncelle

### Cloudflare Pages Dashboard:
1. **Workers & Pages** > **Your Project** > **Settings** > **Environment Variables**
2. Aşağıdaki değişkenleri güncelleyin:

```bash
# Keycloak URL (Tunnel URL'den)
VITE_KEYCLOAK_BASE_URL=https://[yeni-tunnel-url].trycloudflare.com
# VEYA
VITE_KEYCLOAK_BASE_URL=https://keycloak.iyihislerapp.com

# Keycloak Endpoints
VITE_KEYCLOAK_GET_TOKEN_URL=${VITE_KEYCLOAK_BASE_URL}/keycloak/getToken
VITE_KEYCLOAK_GET_USER_INFO_URL=${VITE_KEYCLOAK_BASE_URL}/keycloak/userInfo
```

### Redeploy Frontend:
- **Deployments** sekmesi > **Retry deployment** veya yeni commit push edin

## 🧪 5. Test Et

### Backend Log'larını İzle:
```bash
cd /Users/fs648/Desktop/Keycloak
tail -f keycloak.log
```

### Frontend'den Login Denemesi:
1. Frontend'e git: `https://1b836a21.psikohekimfrontend.pages.dev`
2. Login sayfasına git
3. Giriş yapmayı dene
4. Backend log'larında şunu görmelisiniz:
   ```
   🔍 CORS Filter - Request: OPTIONS /keycloak/getToken | Origin: https://1b836a21.psikohekimfrontend.pages.dev
   ✅ Origin ALLOWED by pattern: ...
   ✅ CORS headers SET for origin: ...
   ```

## 🔍 Sorun Giderme

### Eğer Tunnel URL'i değiştiyse:
1. Yeni URL'i Cloudflare Pages environment variables'a ekleyin
2. Frontend'i redeploy edin

### Eğer CORS hatası devam ediyorsa:
1. Backend log'larını kontrol edin
2. Filter'ın çalışıp çalışmadığını kontrol edin
3. Origin header'ının doğru geldiğini kontrol edin

### Eğer istekler ulaşmıyorsa:
1. Tunnel çalışıyor mu? `./check-tunnel.sh`
2. Keycloak servisi çalışıyor mu? `curl http://localhost:6700/actuator/health`
3. Port doğru mu? (6700)

