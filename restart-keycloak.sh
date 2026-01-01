#!/bin/bash

# Keycloak Servisini Restart Et
# Bu script Keycloak servisini build edip restart eder

echo "🔨 Building Keycloak service..."
cd "$(dirname "$0")"

# Maven build
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build completed!"
echo ""
echo "📋 Next steps:"
echo "   1. Find the Keycloak service process running on port 6700"
echo "   2. Stop it (kill the process or Ctrl+C)"
echo "   3. Start it again: ./mvnw spring-boot:run"
echo ""
echo "   OR if using IDE, restart the Spring Boot application"
echo ""
echo "🔍 To find the process:"
echo "   lsof -i :6700"

