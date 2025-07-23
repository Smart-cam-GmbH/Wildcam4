# Camera Images

This project is a simple mobile FTP image viewer. It provides a web interface built with Vite and an Android application using Gradle.

## Running the Web Viewer

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the development server:
   ```bash
   npm run dev
   ```
   The viewer will be available at the local URL printed by Vite.

## Building/Running the Android App

Use the Gradle wrapper to build or install the Android application:

```bash
./gradlew assembleDebug      # Build the debug APK
./gradlew installDebug       # Install on a connected device or emulator
```

You can also open the project in Android Studio for a full development environment.
