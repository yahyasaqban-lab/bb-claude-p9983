#!/bin/bash
# Manual build script for BlackBerry Claude APK
# This script attempts to build without full Android SDK installation

set -e

echo "=== BlackBerry Claude Manual Build Script ==="
echo ""

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 8 or 11 first."
    echo ""
    echo "Quick install options:"
    echo "1. Download and extract portable Java:"
    echo "   wget -O java.tgz https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u432-b06/OpenJDK8U-jdk_x64_linux_hotspot_8u432b06.tar.gz"
    echo "   tar -xzf java.tgz"
    echo "   export JAVA_HOME=\$(pwd)/jdk8u432-b06"
    echo "   export PATH=\$JAVA_HOME/bin:\$PATH"
    echo ""
    echo "2. Use GitHub Actions (recommended):"
    echo "   git remote add origin https://github.com/YOUR_USERNAME/bb-claude.git"
    echo "   git push -u origin master"
    echo "   # APK will be built automatically and available in Actions artifacts"
    echo ""
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -1)"

# Check if ANDROID_HOME is set
if [ -z "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME not set."
    echo ""
    echo "Options to continue:"
    echo "1. Local Android SDK setup:"
    echo "   - Download Android SDK command line tools"
    echo "   - Install API 18 platform and build tools 25.0.3"
    echo "   - Set ANDROID_HOME environment variable"
    echo ""
    echo "2. Use GitHub Actions build (recommended for this old API version):"
    echo "   - Push to GitHub and let CI build the APK"
    echo "   - More reliable for legacy Android versions"
    echo ""
    exit 1
fi

echo "✅ ANDROID_HOME: $ANDROID_HOME"

# Try building with Gradle
echo ""
echo "🔨 Starting Gradle build..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 Build successful!"
    
    APK_PATH=$(find app/build/outputs -name "*.apk" | head -1)
    if [ -n "$APK_PATH" ]; then
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
        echo "📱 APK created: $APK_PATH ($APK_SIZE)"
        echo ""
        echo "Installation instructions:"
        echo "1. Transfer to BlackBerry P'9983: $APK_PATH"
        echo "2. Enable 'Unknown sources' in Settings > Security"
        echo "3. Install using file manager"
        echo "4. Enter Claude API key in app settings"
    else
        echo "⚠️  Build completed but APK not found in expected location"
    fi
else
    echo ""
    echo "❌ Build failed. Consider using GitHub Actions for reliable builds."
    echo ""
    echo "GitHub Actions setup:"
    echo "1. git remote add origin https://github.com/YOUR_USERNAME/bb-claude.git"
    echo "2. git push -u origin master"
    echo "3. Check Actions tab for build status and download APK"
fi