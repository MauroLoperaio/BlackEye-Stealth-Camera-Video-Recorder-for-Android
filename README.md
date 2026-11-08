# BlackEye: Stealth Camera & Video Recorder for Android

**BlackEye** is a lightweight, background-focused Android application designed for discreet, hands-free media capture using physical device buttons (Volume Keys). Utilizing an OLED/pOLED-optimized pure black user interface (`#000000`), the application ensures zero display power consumption and complete visual invisibility while capturing photos and recording videos.

## 🌟 Key Features

* **AMOLED / pOLED Pure Black UI:** Implements a `#000000` canvas to ensure pixels remain completely powered off on OLED displays, mimicking a screen-off state.

* **Hardware Button Triggering:**

  * **Volume Up (`KEY_VOLUME_UP`):** Captures a high-resolution photo instantly in the background.

  * **Volume Down (`KEY_VOLUME_DOWN`):** Toggles video recording (Start / Stop) seamlessly.

* **Full Immersive Stealth Mode:** Completely hides system navigation bars, status bars, and notification overlays.

* **Foreground Service Architecture:** Offloads camera logic to an Android Foreground Service to minimize Activity overhead and ensure stability.

* **Zero Sound / Vibration Feedback:** Silent execution tailored for zero audio or haptic emissions.

## 📋 System Requirements & Compatibility

* **Target Device:** Tested & optimized for **Motorola Edge 50 Fusion** (pOLED Display).

* **Operating System:** Android 8.0 (API Level 26) up to Android 14 (API Level 34).

* **Display Requirement:** OLED / AMOLED / pOLED display recommended for true pixel-off screen hiding.

## 🛠️ Architecture & Permissions

### Permissions Required (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />
```

## 🚀 How to Build & Install

### Option A: Building via Terminal / GitHub Codespaces

1. Clone the repository:

   ```bash
   git clone https://github.com/michelegiglio275-wq/BlackEye.git
   cd BlackEye
   ```

2. Grant execution permissions to Gradle:

   ```bash
   chmod +x gradlew
   ```

3. Build the Debug APK:

   ```bash
   ./gradlew assembleDebug --no-daemon
   ```

4. Locate the generated APK at:
   `app/build/outputs/apk/debug/app-debug.apk`

## 🎮 How to Use

1. Launch **BlackEye** on your device.

2. The display will immediately enter an all-black immersive full-screen mode.

3. **To Capture a Photo:** Press the **Volume UP** physical button.

4. **To Record a Video:** Press the **Volume DOWN** physical button to begin recording. Press **Volume DOWN** again to stop and save the video.

5. All media files are saved automatically to the device's default storage path (`Pictures/` and `Movies/`).

## 🔒 Android Privacy Indicators Note (Android 12+)

Starting from Android 12, Google enforced system-level privacy indicators managed by `SystemUI`:

* **App Open / Standby:** Entire display remains black (0% light emission on OLED).

* **Photo Capture:** A green privacy dot will appear at the top-right corner briefly (~1 second) during active frame processing.

* **Video Recording:** The green indicator stays visible throughout active recording sessions.

## ⚠️ Legal Disclaimer

This project is created strictly for personal security, educational research, and hardware utility testing. Capturing audio, video, or photography of individuals without explicit consent may violate local privacy, wiretapping, or surveillance laws in your region. The developer assumes no responsibility for misuse of this software.

---

# BlackEye: Fotocamera Stealth & Registratore Video per Android

**BlackEye** è un'applicazione Android leggera e focalizzata sull'esecuzione in background, progettata per la cattura discreta di foto e video tramite i tasti fisici del dispositivo (tasti del volume). Utilizzando un'interfaccia utente nero puro (`#000000`) ottimizzata per schermi OLED/pOLED, l'applicazione garantisce zero consumo energetico dello schermo e una completa invisibilità visiva durante lo scatto di foto o la registrazione di video.

## 🌟 Caratteristiche Principali

* **UI Nero Puro AMOLED / pOLED:** Implementa uno sfondo `#000000` per garantire che i pixel rimangano completamente spenti sui display OLED, simulando lo stato di schermo spento.

* **Attivazione tramite Tasti Fisici:**

  * **Volume Su (`KEY_VOLUME_UP`):** Scatta all'istante una foto ad alta risoluzione in background.

  * **Volume Giù (`KEY_VOLUME_DOWN`):** Avvia o interrompe la registrazione video (Start / Stop).

* **Modalità Stealth Immersiva:** Nasconde completamente la barra di navigazione di sistema, la barra di stato e le sovrapposizioni delle notifiche.

* **Architettura a Foreground Service:** Delega la logica della fotocamera a un servizio in background di Android per ridurre il carico dell'Activity e garantire massima stabilità.

* **Zero Suoni / Vibrazioni:** Esecuzione completamente silenziosa senza alcun feedback audio o tattile.

## 📋 Requisiti di Sistema e Compatibilità

* **Dispositivo Target:** Testato e ottimizzato per **Motorola Edge 50 Fusion** (Display pOLED).

* **Sistema Operativo:** Da Android 8.0 (API Level 26) fino ad Android 14 (API Level 34).

* **Requisito Display:** Schermo OLED / AMOLED / pOLED consigliato per lo spegnimento effettivo dei pixel.

## 🛠️ Architettura e Permessi

### Permessi Richiesti (`AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MICROPHONE" />
```

## 🚀 Come Compilare e Installare

### Compilazione tramite Terminale / GitHub Codespaces

1. Clona la repository:

   ```bash
   git clone https://github.com/michelegiglio275-wq/BlackEye.git
   cd BlackEye
   ```

2. Configura i permessi di esecuzione per Gradle:

   ```bash
   chmod +x gradlew
   ```

3. Compila il file APK di Debug:

   ```bash
   ./gradlew assembleDebug --no-daemon
   ```

4. Trova l'APK generato nel percorso:
   `app/build/outputs/apk/debug/app-debug.apk`

## 🎮 Come Usare l'App

1. Avvia **BlackEye** sul tuo dispositivo.

2. Lo schermo diventerà immediatamente nero puro in modalità a schermo intero.

3. **Per Scattare una Foto:** Premi il tasto fisico **Volume SU**.

4. **Per Registrare un Video:** Premi il tasto fisico **Volume GIÙ** per avviare la registrazione. Premi nuovamente **Volume GIÙ** per interrompere e salvare il video.

5. Tutti i file multimediali vengono salvati automaticamente nelle cartelle predefinite di sistema (`Pictures/` e `Movies/`).

## 🔒 Nota sugli Indicatori di Privacy Android (Android 12+)

A partire da Android 12, Google ha introdotto indicatori di privacy a livello di sistema gestiti da `SystemUI`:

* **App Aperta / In Attesa:** L'intero schermo rimane nero (0% di emissione luminosa su schermi OLED).

* **Scatto Foto:** Un pallino verde di privacy apparirà brevemente in alto a destra (~1 secondo) durante l'elaborazione del fotogramma.

* **Registrazione Video:** L'indicatore verde rimane visibile per tutta la durata della registrazione attiva.

## ⚠️ Avviso Legale / Disclaimer

Questo progetto è stato creato esclusivamente per scopi di sicurezza personale, ricerca educativa e test delle funzionalità hardware. La registrazione o lo scatto di foto/video a persone senza l'esplicito consenso può violare le leggi locali sulla privacy e sulla sorveglianza. Lo sviluppatore non si assume alcuna responsabilità per l'uso improprio di questo software.