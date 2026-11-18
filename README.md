# BlackEye — Stealth Camera & Video Recorder for Android

🌐 **Lingua / Language:** [🇮🇹 Italiano](#-italiano) | [🇬🇧 English](#-english)

---

## 🇮🇹 Italiano

**BlackEye** è un'applicazione Android progettata per scattare fotografie e registrare video rapidamente tramite i tasti fisici del volume, sia in background mentre utilizzi altre app, sia con una modalità a schermo completamente nero.

### ✨ Cosa fa l'app

* **Configurazione indipendente per Foto e Video:** permette di scegliere separatamente quale fotocamera utilizzare (**Anteriore** o **Posteriore**) per i video e quale per le foto.
* **Controllo tramite tasti fisici ovunque:** grazie a un servizio di accessibilità dedicato, intercetta i tasti del volume dalla schermata Home o all'interno di qualsiasi altra applicazione:
  * **Volume Su (o Freccia Su):** scatta immediatamente una foto con la fotocamera configurata nella sezione *Foto*.
  * **Volume Giù:** avvia la registrazione video con la fotocamera configurata nella sezione *Video*; una seconda pressione interrompe e salva il filmato.
* **Funzionamento silenzioso in background:** non mostra anteprime, finestre sovrapposte, suoni dell'otturatore o vibrazioni durante l'acquisizione.
* **Modalità Schermo Spento (Schermo Nero persistente):** oscura completamente lo schermo (`#000000`, inclusa l'area attorno alla fotocamera frontale), imposta la luminosità al minimo e nasconde le barre di sistema. I tasti del volume restano attivi e lo schermo rimane nero finché non si esegue un **triplo tocco** sullo schermo.
* **Salvataggio automatico in galleria:** tutti i file vengono salvati direttamente nella cartella `DCIM/StealthCam` del dispositivo in formato `.jpg` e `.mp4`.

---

### 📂 Struttura del Repository

```text
├── BlackEye-apk/                  # Cartella contenente il file .apk pronto da installare
├── BlackEye-StealthCam-v3/        # Codice sorgente del progetto Android (Kotlin / CameraX)
├── .github/workflows/             # Workflow GitHub Actions per la compilazione automatica
└── README.md
```

---

### 📥 Come scaricarla e installarla

1. Apri la cartella **[`BlackEye-apk/`](./BlackEye-apk/)** in questo repository.
2. Clicca sul file **`.apk`** presente al suo interno e premi il pulsante **Download** (oppure **View raw**) per scaricarlo sul tuo smartphone Android.
3. Apri il file `.apk` scaricato dal file manager o dal browser e conferma l'installazione (se richiesto da Android, attiva l'opzione **Consenti da questa origine**).

#### ⚠️ Attivazione dei tasti rapidi (Android 13 / 14 / 15)
Per permettere ai tasti **Volume Su** e **Volume Giù** di funzionare anche fuori dall'app, è necessario abilitare il servizio di accessibilità:
1. Apri **BlackEye** e concedi i permessi per **Fotocamera** e **Microfono**.
2. Tocca il pulsante **ATTIVA TASTI RAPIDI IN ACCESSIBILITÀ** nella parte inferiore della schermata.
3. Nella lista *App scaricate*, tocca **BlackEye - Tasti Rapidi Fotocamera**.
4. *Nota per Android 13 e versioni successive:* se l'opzione risulta disattivata con l'avviso *"Impostazione con restrizioni"*:
   * Tocca la voce disattivata e premi **Chiudi** sull'avviso.
   * <img width="675" height="465" alt="image" src="https://github.com/user-attachments/assets/075dbca4-9429-409d-9149-9ae6f801c21f" />
   * <img width="615" height="780" alt="image" src="https://github.com/user-attachments/assets/e2c1f232-6d29-46fa-a902-07b299d6bdb6" />

   * Vai nella **Schermata Home nel quale si trova la app e tenendo premuto su di esso, clicca su: Informazioni App** 
   * <img width="465" height="465" alt="image" src="https://github.com/user-attachments/assets/d04d6565-9eb9-4c42-8e98-08932dacd004" />

   * Tocca i **tre puntini (`⋮`)** in alto a destra (o scorri in fondo alla pagina) e seleziona **Consenti impostazioni con restrizioni**, confermando con PIN o impronta digitale.
   * <img width="675" height="672" alt="image" src="https://github.com/user-attachments/assets/6e9e0731-1d94-42e9-873d-0788976a74ee" />
   * <img width="675" height="484" alt="image" src="https://github.com/user-attachments/assets/29c6badf-6de8-44e1-bcd2-58ed9ff8a64e" />


   * Torna in **Impostazioni → Accessibilità → BlackEye - Tasti Rapidi Fotocamera** e attiva l'interruttore.
   * <img width="675" height="1185" alt="image" src="https://github.com/user-attachments/assets/db658759-1367-45c8-9fc0-6ba46a0a9ec5" />
   * <img width="675" height="480" alt="image" src="https://github.com/user-attachments/assets/dd8f6cd1-1dc6-45a3-a16d-f7f7e75029df" />

---

### 📱 Come si usa

<img width="675" height="1177" alt="image" src="https://github.com/user-attachments/assets/34aaa37d-d448-463c-8498-fe09be330d10" />


1. **Imposta le fotocamere:**
   * Nella sezione **VIDEO (TASTO VOLUME GIÙ)** scegli **Anteriore** o **Posteriore**.
   * Nella sezione **FOTO (TASTO VOLUME SU)** scegli **Anteriore** o **Posteriore**.
   * Le preferenze vengono salvate automaticamente.
2. **Scegli la modalità schermo:**
   * **Schermo Acceso:** premi **ATTIVA IN BACKGROUND (ESCI DALL'APP)**. L'app si riduce in background e puoi usare normalmente il telefono; premi **Volume Su** per scattare una foto o **Volume Giù** per avviare/fermare un video in qualsiasi momento.
   * **Schermo Spento:** premi **ATTIVA SCHERMO NERO**. Lo schermo diventa completamente nero con luminosità al minimo. Usa **Volume Su** e **Volume Giù** senza che compaia nulla a schermo. Per uscire dalla schermata nera e tornare ai controlli, esegui un **triplo tocco rapido** sullo schermo.

---

### 🛠️ Compilazione dai sorgenti (Opzionale)

Il repository include un workflow GitHub Actions (`.github/workflows/build-apk.yml`) che compila automaticamente l'APK ad ogni modifica del codice sorgente utilizzando JDK 17, Gradle 8.6 e Android SDK 34.

---

### ⚖️ Avvertenze legali

Questa applicazione è stata realizzata a scopo educativo e di utilità personale. L'utente è l'unico responsabile del rispetto delle leggi vigenti in materia di privacy, riservatezza e consenso alla registrazione audio/video nel proprio Paese.

---
---

## 🇬🇧 English

**BlackEye** is an Android application designed to capture photos and record videos quickly using the physical volume buttons, either silently in the background while using other apps or with a persistent black-screen mode.

### ✨ What It Does

* **Independent Camera Selection for Photos & Videos:** Lets you configure the **Front** or **Back** camera separately for video recording and photo capture.
* **Global Hardware Button Triggers:** Uses an Android Accessibility Service to intercept volume key presses from the Home screen or inside any running app:
  * **Volume Up (or D-Pad Up):** Instantly takes a photo using the camera selected in the *Photo* section.
  * **Volume Down:** Starts recording a video using the camera selected in the *Video* section; pressing it again stops and saves the recording.
* **Silent Background Operation:** Captures media without previews, pop-ups, shutter sounds, or vibrations.
* **Screen Off Mode (Persistent Black Screen):** Turns the entire screen pure black (`#000000`, including the front camera cutout area), sets screen brightness to minimum, and hides system navigation/status bars. Volume buttons remain active and the screen stays completely black until you perform a **triple-tap** on the display.
* **Automatic Gallery Storage:** Saves all captured photos (`.jpg`) and videos (`.mp4`) directly to `DCIM/StealthCam`.

---

### 📂 Repository Structure

```text
├── BlackEye-apk/                  # Folder containing the ready-to-install .apk file
├── BlackEye-StealthCam-v3/        # Android project source code (Kotlin / CameraX)
├── .github/workflows/             # GitHub Actions workflow for automated APK builds
└── README.md
```

---

### 📥 Download & Installation

1. Open the **[`BlackEye-apk/`](./BlackEye-apk/)** folder in this repository.
2. Click on the **`.apk`** file inside and press **Download** (or **View raw**) to save it to your Android device.
3. Open the downloaded `.apk` file and complete the installation (enable **Allow from this source** if prompted by Android).

#### ⚠️ Enabling Hardware Shortcuts (Android 13 / 14 / 15)
To allow **Volume Up** and **Volume Down** to work outside the app, enable the Accessibility Service:

1. Open **BlackEye** and grant **Camera** and **Microphone** permissions.
2. Tap **ATTIVA TASTI RAPIDI IN ACCESSIBILITÀ** at the bottom of the screen.
3. Under *Downloaded apps*, tap **BlackEye - Tasti Rapidi Fotocamera**.
4. *Note for Android 13 and newer:* If the toggle is grayed out with a *"Restricted setting"* notice:
   * Tap the grayed-out entry and tap **Close** on the dialog.
   * Open Android **Settings → Apps → BlackEye** (App info).
   * Tap the **three-dot menu (`⋮`)** in the top-right corner (or scroll to the bottom of the page) and select **Allow restricted settings**, then authenticate with your PIN or fingerprint.
   * Return to **Settings → Accessibility → BlackEye - Tasti Rapidi Fotocamera** and turn the switch **On**.

---

### 📱 How to Use

1. **Configure Cameras:**
   * Under **VIDEO (TASTO VOLUME GIÙ)**, select **Anteriore** (Front) or **Posteriore** (Back).
   * Under **FOTO (TASTO VOLUME SU)**, select **Anteriore** (Front) or **Posteriore** (Back).
   * Selections are saved automatically.
2. **Choose Screen Mode:**
   * **Schermo Acceso (Screen On):** Tap **ATTIVA IN BACKGROUND (ESCI DALL'APP)**. The app moves to the background so you can use your phone normally; press **Volume Up** to take a photo or **Volume Down** to start/stop video recording from any app.
   * **Schermo Spento (Screen Off):** Tap **ATTIVA SCHERMO NERO**. The screen turns completely black at minimum brightness. Use **Volume Up** and **Volume Down** with nothing appearing on screen. To exit black-screen mode and return to the controls, **triple-tap** anywhere on the screen.

---

### 🛠️ Building from Source (Optional)

This repository includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that automatically builds the debug APK on every push using JDK 17, Gradle 8.6, and Android SDK 34.

---

### ⚖️ Disclaimer

This software is provided for educational and personal utility purposes. Users are solely responsible for complying with all applicable local laws and regulations regarding privacy and audio/video recording consent.
