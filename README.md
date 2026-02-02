# BlackBerry Claude Chat App

A Claude AI chat application designed specifically for BlackBerry P'9983 (Android API 18).

## Features
- Clean chat interface optimized for BlackBerry devices
- Direct integration with Claude AI via Anthropic API
- Local message storage using SQLite
- Minimal dependencies for maximum compatibility with API 18

## Building the APK

### Option 1: Local Build (requires setup)
1. Install Java JDK 8 or 11
2. Install Android SDK with API 18 and build tools 25.0.3
3. Set ANDROID_HOME environment variable
4. Run: `./gradlew assembleDebug`

### Option 2: Cloud Build (recommended)
1. Push this repository to GitHub
2. GitHub Actions will automatically build the APK
3. Download the APK from the Actions artifacts

### Quick GitHub Setup
```bash
# Create a new repository on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/bb-claude.git
git push -u origin master
```

## Installation on BlackBerry P'9983
1. Enable "Unknown sources" in Settings > Security
2. Transfer the APK file to your device
3. Install using a file manager
4. Enter your Claude API key in the settings

## Configuration
- Get your Claude API key from: https://console.anthropic.com/
- Open the app and tap Settings to enter your API key
- The app will remember your key and chat history locally

## Technical Details
- Target SDK: Android API 18 (Android 4.3)
- Minimum SDK: API 18
- Build tools: 25.0.3
- Gradle: 4.10.3
- Architecture: Optimized for older Android devices

## File Structure
- `app/src/main/java/` - Java source code
- `app/src/main/res/` - Android resources (layouts, strings, etc.)
- `app/build.gradle` - App-specific build configuration
- `.github/workflows/` - GitHub Actions build automation