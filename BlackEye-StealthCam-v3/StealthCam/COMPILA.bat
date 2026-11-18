@echo off
chcp 65001 >nul
title StealthCam Builder

echo.
echo =============================================
echo   StealthCam - Script di build automatico
echo =============================================
echo.

REM ---- Controlla Java ----
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERRORE] Java non trovato!
    echo Scarica JDK 17 da: https://adoptium.net
    echo Installa e poi riesegui questo script.
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1') do echo Java trovato: %%i && goto :java_ok
:java_ok

REM ---- Controlla ANDROID_HOME ----
if "%ANDROID_HOME%"=="" (
    echo.
    echo [ATTENZIONE] La variabile ANDROID_HOME non e' impostata.
    echo.
    echo Hai gia' installato Android SDK command-line tools?
    echo Se NO: segui la guida nel file guida-stealthcam.md
    echo.
    set /p ANDROID_HOME_INPUT="Inserisci il path dell'SDK Android (es. C:\Android\sdk): "
    set ANDROID_HOME=%ANDROID_HOME_INPUT%
)

echo.
echo Android SDK: %ANDROID_HOME%
echo.

REM ---- Scarica gradle-wrapper.jar se mancante ----
if not exist "%~dp0gradle\wrapper\gradle-wrapper.jar" (
    echo [INFO] Scarico gradle-wrapper.jar...
    powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.6.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%~dp0gradle\wrapper\gradle-wrapper.jar'"
    if %ERRORLEVEL% NEQ 0 (
        echo [ERRORE] Download fallito. Controlla la connessione internet.
        pause
        exit /b 1
    )
)

REM ---- Build APK ----
echo [BUILD] Compilazione in corso...
echo Potrebbe volerci qualche minuto alla prima esecuzione (scarica dipendenze)...
echo.

cd /d "%~dp0"
call gradlew.bat assembleDebug --no-daemon

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRORE] Build fallita! Leggi i messaggi di errore qui sopra.
    pause
    exit /b 1
)

echo.
echo =============================================
echo   BUILD COMPLETATA CON SUCCESSO!
echo =============================================
echo.
echo APK generato in:
echo %~dp0app\build\outputs\apk\debug\app-debug.apk
echo.

REM ---- Copia APK sul Desktop ----
set DESKTOP=%USERPROFILE%\Desktop
copy "%~dp0app\build\outputs\apk\debug\app-debug.apk" "%DESKTOP%\StealthCam.apk" >nul
echo APK copiato anche sul Desktop: %DESKTOP%\StealthCam.apk
echo.

REM ---- Installa via ADB se telefono collegato ----
set ADB=%ANDROID_HOME%\platform-tools\adb.exe
if exist "%ADB%" (
    echo Cerco telefono collegato via USB...
    "%ADB%" devices
    echo.
    set /p INSTALL="Vuoi installare l'APK sul telefono ora? (s/n): "
    if /i "%INSTALL%"=="s" (
        "%ADB%" install -r "%DESKTOP%\StealthCam.apk"
        echo.
        echo App installata! Aprila dal telefono.
    )
) else (
    echo [INFO] ADB non trovato. Copia manualmente l'APK sul telefono.
)

echo.
pause
