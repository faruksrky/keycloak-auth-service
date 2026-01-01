# 🧪 CORS Test Rehberi

## ✅ Kontrol Edilecekler

### 1. Backend Çalışıyor mu?
```bash
curl http://localhost:6700/actuator/health
```
Veya browser'da: `http://localhost:6700/actuator/health`

### 2. Tunnel Çalışıyor mu?
```bash
curl https://keycloak.iyihislerapp.com/actuator/health
```
Veya browser'da: `https://keycloak.iyihislerapp.com/actuator/health`

### 3. CORS Test
Browser console'da (frontend'den):
```javascript
fetch('https://keycloak.iyihislerapp.com/keycloak/getToken', {
  method: 'OPTIONS',
  headers: {
    'Origin': 'https://1b836a21.psikohekimfrontend.pages.dev',
    'Access-Control-Request-Method': 'POST',
    'Access-Control-Request-Headers': 'Content-Type'
  }
}).then(r => {
  console.log('CORS Headers:', {
    'Access-Control-Allow-Origin': r.headers.get('Access-Control-Allow-Origin'),
    'Access-Control-Allow-Credentials': r.headers.get('Access-Control-Allow-Credentials'),
    'Access-Control-Allow-Methods': r.headers.get('Access-Control-Allow-Methods'),
    status: r.status
  });
}).catch(e => console.error('CORS Error:', e));
```

## 📋 Backend Log'larını İzleme

Terminal'de:
```bash
cd /Users/fs648/Desktop/Keycloak
tail -f keycloak.log | grep -i "cors\|filter\|OPTIONS"
```

## ✅ Başarılı CORS Response

Eğer CORS doğru çalışıyorsa, şunları görmelisiniz:

**Backend Log:**
```
🔍 CORS Filter - Request: OPTIONS /keycloak/getToken | Origin: https://1b836a21.psikohekimfrontend.pages.dev
✅ Origin ALLOWED by pattern: https://.*\.psikohekimfrontend\.pages\.dev | Origin: https://1b836a21.psikohekimfrontend.pages.dev
✅ OPTIONS preflight ALLOWED, returning 200 OK
```

**Browser Response Headers:**
```
Access-Control-Allow-Origin: https://1b836a21.psikohekimfrontend.pages.dev
Access-Control-Allow-Credentials: true
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Access-Control-Allow-Headers: *
```

## 🔍 Sorun Giderme

### Eğer CORS hatası devam ediyorsa:

1. **Backend log'larında hiçbir şey görmüyorsanız:**
   - İstekler backend'e ulaşmıyor demektir
   - Tunnel'ı kontrol edin

2. **"Origin NOT ALLOWED" görüyorsanız:**
   - Origin header'ını kontrol edin
   - Pattern'lerin doğru olduğundan emin olun

3. **"OPTIONS preflight REJECTED" görüyorsanız:**
   - Origin pattern'i eşleşmiyor
   - Log'lardaki origin'i kontrol edin

