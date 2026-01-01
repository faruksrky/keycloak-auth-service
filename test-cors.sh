#!/bin/bash

# Keycloak CORS Test Script
# Bu script Keycloak servisinin CORS header'larını test eder

echo "🧪 Testing Keycloak CORS Configuration..."
echo ""

KEYCLOAK_URL="http://localhost:6700"
FRONTEND_ORIGIN="https://54e90153.psikohekimfrontend.pages.dev"

echo "📍 Keycloak URL: $KEYCLOAK_URL"
echo "📍 Frontend Origin: $FRONTEND_ORIGIN"
echo ""

# OPTIONS preflight request test
echo "1️⃣ Testing OPTIONS (preflight) request..."
echo ""

RESPONSE=$(curl -s -X OPTIONS "$KEYCLOAK_URL/keycloak/getToken" \
  -H "Origin: $FRONTEND_ORIGIN" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -i)

echo "$RESPONSE" | grep -i "access-control"

if echo "$RESPONSE" | grep -qi "access-control-allow-origin"; then
    echo ""
    echo "✅ CORS headers found!"
    echo ""
    echo "Response headers:"
    echo "$RESPONSE" | head -20
else
    echo ""
    echo "❌ CORS headers NOT found!"
    echo ""
    echo "Full response:"
    echo "$RESPONSE" | head -30
fi

echo ""
echo "2️⃣ Testing if service is running..."
if curl -s -f "$KEYCLOAK_URL/error" > /dev/null 2>&1; then
    echo "✅ Keycloak service is running on port 6700"
else
    echo "❌ Keycloak service is NOT running on port 6700"
    echo "   Please start the service first: cd /Users/fs648/Desktop/Keycloak && ./mvnw spring-boot:run"
fi

