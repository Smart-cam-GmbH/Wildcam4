# Wildcam Viewer

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

### Configuration

FTP connection details are no longer hard coded. Edit `public/config.json` to
change the host, username, password and folder used by the web viewer. The
Android application reads the same values from
`app/src/main/res/raw/ftp_config.json`. These JSON files are read at runtime, so
you can update them without rebuilding the project.

## Building/Running the Android App

Use the Gradle wrapper to build or install the Android application:

```bash
./gradlew assembleDebug      # Build the debug APK
./gradlew installDebug       # Install on a connected device or emulator
```

You can also open the project in Android Studio for a full development environment.

## Sending SMS Commands

The Android app includes a simple interface for sending predefined SMS commands.

1. Grant the **Send SMS** permission when prompted.
2. From the main screen, open the menu and choose **SMS Commands**.
3. Enter the destination phone number and pick a command from the list.
4. Tap **Send** to transmit the SMS.

Commands can be adjusted in `SMSCommandActivity` if needed.

## License

This project is licensed under the [MIT License](LICENSE). By contributing, you agree that your contributions will be licensed under the same terms.

