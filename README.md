# Camera Images

This project is a simple mobile FTP image viewer. It provides a web interface built with Vite and an Android application using Gradle.

## Configuration

Both the web viewer and Android app read their FTP connection details from a `.env` file in the project root. Copy the provided `.env.example` file and adjust the values as needed:

```bash
cp .env.example .env
```

The available variables are:

```
VITE_FTP_HOST=your.ftp.host
VITE_FTP_USERNAME=username
VITE_FTP_PASSWORD=password
VITE_FTP_FOLDER=/remote/path
```

During development Vite loads these values automatically. Gradle also reads the same file and exposes them to the Android app via `BuildConfig`.

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
