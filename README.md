<div align="center">

# 👁️ BlackEye (StealthCam)

**Discreet Background Camera & Video Recorder for Android**  
**Fotocamera e Videoregistratore Discreto in Background per Android**

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026--34)-brightgreen.svg)]()
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)]()
[![CameraX](https://img.shields.io/badge/Engine-AndroidX%20CameraX-blue.svg)]()

[🇮🇹 Leggi in Italiano](#-italiano) • [🇬🇧 Read in English](#-english)

</div>

---

<a name="-italiano"></a>
## 🇮🇹 Italiano

### 📌 Cos'è BlackEye e cosa fa
**BlackEye** è un'applicazione Android progettata per scattare fotografie e registrare video in modo rapido e discreto, operando completamente in background senza mostrare anteprime sullo schermo, finestre pop-up, suoni dell'otturatore o vibrazioni.

L'app utilizza un **Servizio di Accessibilità** integrato per intercettare i tasti fisici del volume in qualsiasi momento: puoi scattare foto o avviare/fermare registrazioni video mentre ti trovi nella schermata Home, mentre usi altre app (browser, chat, social, ecc.) oppure con lo schermo completamente oscurato.

---

### ✨ Funzionalità principali

1. **Configurazione indipendente per Foto e Video**
   * **Sezione Video (`Tasto Volume Giù`):** permette di scegliere in modo indipendente se registrare con la fotocamera **Anteriore** o **Posteriore**.
   * **Sezione Foto (`Tasto Volume Su`):** permette di scegliere in modo indipendente se scattare con la fotocamera **Anteriore** o **Posteriore**.
   * Le preferenze vengono salvate automaticamente ad ogni modifica.

2. **Tasti fisici attivi ovunque (in qualsiasi app)**
   * **Premi `Volume Su` (o `Freccia Su`):** scatta istantaneamente una foto silenziosa con la fotocamera configurata per le foto. *(Se è già in corso una registrazione video, il tasto viene ignorato per non interrompere il video).*
   * **Premi `Volume Giù`:** avvia la registrazione video con audio usando la fotocamera configurata per i video. Premendo nuovamente **`Volume Giù`**, la registrazione viene interrotta e salvata automaticamente.
   * **Zero segnali visivi o sonori:** nessun suono di scatto, nessuna vibrazione e nessun messaggio a schermo.

3. **Due modalità schermo selezionabili**
   * **Schermo Acceso (`ATTIVA IN BACKGROUND`):** chiude l'interfaccia dell'app lasciando attivo il servizio in background. Puoi utilizzare normalmente il telefono e premere i tasti volume in qualsiasi momento per scattare o registrare.
   * **Schermo Spento (`ATTIVA SCHERMO NERO`):** trasforma l'intero display in una schermata completamente nera (`#000000`), abbassa la luminosità al minimo assoluto, nasconde le barre di stato e di navigazione (coprendo anche il foro della fotocamera frontale) e blocca il tasto Indietro per evitare uscite accidentali.
     * Durante la schermata nera puoi premere `Volume Su` e `Volume Giù` tutte le volte che vuoi: lo schermo resterà sempre nero senza alcuna notifica o avviso sovrapposto.
     * Per uscire dallo schermo nero e tornare all'app, esegui un **triplo tocco rapido** sullo schermo.

4. **Salvataggio automatico in Galleria**
   * Tutti i file vengono salvati in alta qualità nella memoria interna del telefono al percorso:
     `Memoria interna / DCIM / StealthCam`
   * **Formato Foto:** `BE_FRONT_AAAAMMGG_HHMMSS.jpg` oppure `BE_BACK_AAAAMMGG_HHMMSS.jpg`
   * **Formato Video:** `BE_VID_FRONT_AAAAMMGG_HHMMSS.mp4` oppure `BE_VID_BACK_AAAAMMGG_HHMMSS.mp4`

---

### 📥 Come scaricare e installare l'app

#### Metodo 1: Download diretto dell'APK pronto (Consigliato)
1. Apri la cartella **`APK/`** presente direttamente nella pagina principale di questo repository.
2. Clicca sul file **`.apk`** all'interno della cartella (es. `BlackEye.apk` o `app-debug.apk`).
3. Clicca sul pulsante **Download** (o **View raw** / icona di scaricamento in alto a destra) per scaricare il file sul tuo smartphone Android.
4. Apri il file `.apk` scaricato dal file manager o dal browser e conferma l'installazione *(se richiesto, attiva l'opzione **"Consenti da questa origine"** per installare app al di fuori del Play Store)*.

#### Metodo 2: Compilazione automatica tramite GitHub Actions
1. Vai nella scheda **Actions** in alto in questo repository.
2. Seleziona l'ultima esecuzione completata con spunta verde del workflow **Build BlackEye APK**.
3. Scorri fino in fondo alla pagina nella sezione **Artifacts** e scarica il pacchetto **`BlackEye-debug-apk`**.
4. Estrai lo ZIP e installa il file `app-debug.apk` sul telefono.

---

### ⚙️ Configurazione iniziale (da fare solo la prima volta)

Per permettere a BlackEye di funzionare in background e riconoscere i tasti del volume in qualsiasi app, segui questi passaggi al primo avvio:

1. **Concedi i permessi di base:**
   * Apri **BlackEye** e accetta i permessi per **Fotocamera** e **Microfono**.
2. **Attiva il Servizio di Accessibilità (Tasti Rapidi):**
   * Nella parte inferiore dell'app, tocca il pulsante **`ATTIVA TASTI RAPIDI IN ACCESSIBILITÀ`**.
   * Cerca la voce **`BlackEye - Tasti Rapidi Fotocamera`** e attivala.
   * ⚠️ **Nota per Android 13, 14 e 15 ("Impostazioni con restrizioni"):**  
     Se la voce appare in grigio con scritto *"Gestita tramite impostazioni con restrizioni"* e mostra l'avviso *"All'app è stato negato l'accesso"*:
     1. Tocca **Chiudi** sull'avviso.
     2. Vai nelle **Impostazioni del telefono → App → BlackEye** (oppure tieni premuta l'icona di BlackEye nella Home e tocca **ⓘ Informazioni app**).
     3. Tocca i **tre puntini (`⋮`)** in alto a destra e seleziona **`Consenti impostazioni con restrizioni`**, confermando con il tuo PIN o impronta digitale.
     4. Torna in **Impostazioni → Accessibilità → BlackEye - Tasti Rapidi Fotocamera** e attiva l'interruttore.
3. **Nascondi completamente la notifica di sistema (Opzionale):**
   * Android richiede un servizio attivo per accedere alla fotocamera in background. La notifica di BlackEye è già impostata come silenziosa e invisibile nella barra di stato, ma se desideri non vederla nemmeno abbassando la tendina delle notifiche: tieni premuto sulla notifica di **BlackEye** nella tendina → **Disattiva notifiche**.

---

### 🚀 Come si usa

1. Apri **BlackEye**.
2. Nella sezione **VIDEO**, scegli **Anteriore** o **Posteriore**.
3. Nella sezione **FOTO**, scegli **Anteriore** o **Posteriore**.
4. Nella sezione **MODALITÀ SCHERMO**, seleziona:
   * **Schermo Acceso** → tocca **`ATTIVA IN BACKGROUND (ESCI DALL'APP)`**: tornerai alla Home e potrai usare il telefono normalmente. Premi `Volume Su` per scattare foto e `Volume Giù` per avviare/fermare video in qualsiasi momento.
   * **Schermo Spento** → tocca **`ATTIVA SCHERMO NERO`**: lo schermo diventerà completamente nero. Usa `Volume Su` per le foto e `Volume Giù` per avviare/fermare i video. Fai un **triplo tocco** sullo schermo nero per tornare al menu.

---

### ⚖️ Avvertenze legali
Questo progetto è stato realizzato esclusivamente a scopo educativo, di ricerca e per uso personale legittimo (es. appunti visivi rapidi o sicurezza personale). L'utente è l'unico responsabile dell'utilizzo dell'applicazione ed è tenuto a rispettare le leggi vigenti sulla privacy e sulla registrazione di immagini/audio nel proprio Paese.

---
---

<a name="-english"></a>
## 🇬🇧 English

### 📌 What is BlackEye & What It Does
**BlackEye** is an Android application designed for fast, discreet photo and video capture. It operates completely in the background without displaying camera previews, pop-up windows, shutter sounds, or vibrations.

By leveraging a built-in **Accessibility Service**, the app intercepts physical volume button presses system-wide: you can snap photos or start/stop video recordings directly from your Home screen, while using any third-party app (browser, messaging, social media, etc.), or while simulating a completely turned-off screen.

---

### ✨ Key Features

1. **Independent Camera Selection for Photos & Videos**
   * **Video Section (`Volume Down`):** Independently choose between the **Front** or **Rear** camera for video recording.
   * **Photo Section (`Volume Up`):** Independently choose between the **Front** or **Rear** camera for taking photos.
   * All preferences are saved automatically as soon as you select them.

2. **Global Hardware Key Shortcuts (Works in Any App)**
   * **Press `Volume Up` (or `DPAD Up`):** Instantly captures a silent photo using the camera configured for photos. *(If a video is currently recording, photo capture is ignored so the recording is not interrupted).*
   * **Press `Volume Down`:** Starts recording video with audio using the camera configured for videos. Pressing **`Volume Down`** again stops and saves the video automatically.
   * **Zero Visual or Audio Cues:** No shutter sound, no vibration, and no on-screen toast messages.

3. **Two Selectable Screen Modes**
   * **Screen On (`ATTIVA IN BACKGROUND`):** Minimizes the app to the background while keeping the service active. You can use your phone normally and press the volume buttons at any time to capture photos or videos.
   * **Screen Off (`ATTIVA SCHERMO NERO`):** Turns the entire display into a persistent pitch-black screen (`#000000`), sets screen brightness to the absolute minimum, hides status and navigation bars (covering camera cutouts/notches as well), and blocks the Back gesture to prevent accidental exits.
     * While in black-screen mode, you can press `Volume Up` and `Volume Down` as many times as needed—the screen remains completely black with no overlapping notifications or alerts.
     * To exit the black screen and return to the app menu, perform a **quick triple-tap** anywhere on the screen.

4. **Automatic Gallery Storage**
   * All media files are saved in high quality to your device's internal storage at:
     `Internal Storage / DCIM / StealthCam`
   * **Photo Naming:** `BE_FRONT_YYYYMMDD_HHMMSS.jpg` or `BE_BACK_YYYYMMDD_HHMMSS.jpg`
   * **Video Naming:** `BE_VID_FRONT_YYYYMMDD_HHMMSS.mp4` or `BE_VID_BACK_YYYYMMDD_HHMMSS.mp4`

---

### 📥 How to Download & Install

#### Method 1: Direct APK Download (Recommended)
1. Open the **`APK/`** folder located directly in the root of this repository.
2. Click on the **`.apk`** file inside (e.g., `BlackEye.apk` or `app-debug.apk`).
3. Click **Download** (or **View raw** / the download icon in the top-right corner) to save the installer to your Android device.
4. Open the downloaded `.apk` file and confirm installation *(if prompted, enable **"Allow from this source"** to allow sideloading)*.

#### Method 2: Automated Build via GitHub Actions
1. Navigate to the **Actions** tab at the top of this repository.
2. Select the latest successful run of the **Build BlackEye APK** workflow.
3. Scroll down to the **Artifacts** section and download **`BlackEye-debug-apk`**.
4. Unzip the archive and install `app-debug.apk` on your Android phone.

---

### ⚙️ Initial Setup (One-Time Configuration)

To allow BlackEye to run in the background and detect hardware volume buttons across all apps, complete these steps after installing:

1. **Grant Core Permissions:**
   * Launch **BlackEye** and grant **Camera** and **Microphone** permissions.
2. **Enable the Accessibility Service (Hardware Shortcuts):**
   * At the bottom of the app, tap **`ATTIVA TASTI RAPIDI IN ACCESSIBILITÀ`**.
   * Locate **`BlackEye - Tasti Rapidi Fotocamera`** and toggle it **ON**.
   * ⚠️ **Note for Android 13, 14 & 15 ("Restricted Settings"):**  
     If the option is grayed out with *"Controlled by restricted settings"* and shows *"Restricted setting: For your security, this setting is currently unavailable"*:
     1. Tap **Close** on the dialog.
     2. Open your phone's **Settings → Apps → BlackEye** (or long-press the BlackEye app icon on your Home screen and tap **ⓘ App info**).
     3. Tap the **three-dot menu (`⋮`)** in the top-right corner and select **`Allow restricted settings`**, then authenticate with your PIN or fingerprint.
     4. Return to **Settings → Accessibility → BlackEye - Tasti Rapidi Fotocamera** and turn the switch **ON**.
3. **Completely Hide the Foreground Service Notification (Optional):**
   * Android requires an active foreground service to access the camera in the background. BlackEye's notification is already configured to be silent and hidden from the status bar, but if you also want to hide it from the notification shade: long-press the **BlackEye** notification in the shade → **Turn off notifications**.

---

### 🚀 How to Use

1. Open **BlackEye**.
2. Under **VIDEO**, choose **Anteriore** (Front) or **Posteriore** (Rear).
3. Under **FOTO**, choose **Anteriore** (Front) or **Posteriore** (Rear).
4. Under **MODALITÀ SCHERMO** (Screen Mode), choose:
   * **Schermo Acceso** (Screen On) → tap **`ATTIVA IN BACKGROUND (ESCI DALL'APP)`**: the app moves to the background so you can use your phone normally. Press `Volume Up` to take photos and `Volume Down` to start/stop video recording at any time.
   * **Schermo Spento** (Screen Off) → tap **`ATTIVA SCHERMO NERO`**: the display turns completely black. Use `Volume Up` for photos and `Volume Down` to start/stop videos. **Triple-tap** the black screen to exit back to the menu.

---

### ⚖️ Legal Disclaimer
This software is provided strictly for educational, research, and lawful personal purposes (such as quick visual note-taking or personal security). You are solely responsible for how you use this application and must comply with all local privacy, consent, and recording laws in your jurisdiction.