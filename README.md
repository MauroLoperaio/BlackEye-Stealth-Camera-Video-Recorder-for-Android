# Black Eye 
BlackEye: Stealth Camera & Video Recorder for Android

BlackEye is a lightweight, background-focused Android application designed for discreet, hands-free media capture using physical device buttons (Volume Keys). Utilizing an OLED/pOLED-optimized pure black user interface (#000000), the application ensures zero display power consumption and complete visual invisibility while capturing photos and recording videos.

🌟 Key Features

AMOLED / pOLED Pure Black UI: Implements a #000000 canvas to ensure pixels remain completely powered off on OLED displays, mimicking a screen-off state.

Hardware Button Triggering:

Volume Up (KEY_VOLUME_UP): Captures a high-resolution photo instantly in the background.

Volume Down (KEY_VOLUME_DOWN): Toggles video recording (Start / Stop) seamlessly.

Full Immersive Stealth Mode: Completely hides system navigation bars, status bars, and notification overlays.

Foreground Service Architecture: Offloads camera logic to an Android Foreground Service to minimize Activity overhead and ensure stability.

Zero Sound / Vibration Feedback: Silent execution tailored for zero audio or haptic emissions.

📋 System Requirements & Compatibility

Target Device: Tested & optimized for Motorola Edge 50 Fusion (pOLED Display).

Operating System: Android 8.0 (API Level 26) up to Android 14 (API Level 34).

Display Requirement: OLED / AMOLED / pOLED display recommended for true pixel-off screen hiding.

🛠️ Architecture & Permissions

Permissions Required (AndroidManifest.xml)

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />


🚀 How to Build & Install

Option A: Building via Terminal / GitHub Codespaces

Clone the repository:

git clone https://github.com/michelegiglio275-wq/BlackEye.git
cd BlackEye


Grant execution permissions to Gradle:

chmod +x gradlew


Build the Debug APK:

./gradlew assembleDebug --no-daemon


Locate the generated APK at:
app/build/outputs/apk/debug/app-debug.apk

🎮 How to Use

Launch BlackEye on your device.

The display will immediately enter an all-black immersive full-screen mode.

To Capture a Photo: Press the Volume UP physical button.

To Record a Video: Press the Volume DOWN physical button to begin recording. Press Volume DOWN again to stop and save the video.

All media files are saved automatically to the device's default storage path (Pictures/ and Movies/).

🔒 Android Privacy Indicators Note (Android 12+)

Starting from Android 12, Google enforced system-level privacy indicators managed by SystemUI:

App Open / Standby: Entire display remains black (0% light emission on OLED).

Photo Capture: A green privacy dot will appear at the top-right corner briefly (~1 second) during active frame processing.

Video Recording: The green indicator stays visible throughout active recording sessions.

⚠️ Legal Disclaimer

This project is created strictly for personal security, educational research, and hardware utility testing. Capturing audio, video, or photography of individuals without explicit consent may violate local privacy, wiretapping, or surveillance laws in your region. The developer assumes no responsibility for misuse of this software.
