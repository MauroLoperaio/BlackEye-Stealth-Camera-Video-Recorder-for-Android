package com.stealthcam.app

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraService : LifecycleService() {

    private var recording: Recording? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        promoteToForeground()
    }

    private fun promoteToForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val hasMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            val serviceType = when {
                hasCamera && hasMic -> ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                hasCamera -> ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                else -> 0
            }
            if (serviceType != 0) {
                startForeground(NOTIF_ID, notification, serviceType)
            } else {
                startForeground(NOTIF_ID, notification)
            }
        } else {
            startForeground(NOTIF_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        isRunning = true
        promoteToForeground()

        val action = intent?.action
        val useFront = intent?.getBooleanExtra(EXTRA_USE_FRONT_CAMERA, true) ?: true

        when (action) {
            ACTION_TAKE_PHOTO -> takePhoto(useFront)
            ACTION_START_VIDEO -> {
                if (!isRecording) startVideo(useFront)
            }
            ACTION_STOP_VIDEO -> {
                if (isRecording) stopVideo()
            }
            ACTION_TOGGLE_VIDEO -> {
                if (isRecording) stopVideo() else startVideo(useFront)
            }
        }
        return START_STICKY
    }

    private fun takePhoto(useFrontCamera: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (isRecording) {
            vibrate(doublePulse = true)
            return
        }

        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val selector = if (useFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, selector, capture)

                val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val prefix = if (useFrontCamera) "BE_FRONT_" else "BE_BACK_"
                val cv = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$prefix$ts.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/StealthCam")
                    }
                }

                capture.takePicture(
                    ImageCapture.OutputFileOptions.Builder(
                        contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cv
                    ).build(),
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            vibrate(doublePulse = false)
                            provider.unbindAll()
                            broadcastState("Foto salvata in DCIM/StealthCam")
                        }

                        override fun onError(exception: ImageCaptureException) {
                            provider.unbindAll()
                            broadcastState("Errore scatto foto")
                        }
                    }
                )
            } catch (e: Exception) {
                provider.unbindAll()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startVideo(useFrontCamera: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val qualitySelector = QualitySelector.from(
                Quality.HIGHEST,
                FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
            )
            val recorder = Recorder.Builder()
                .setQualitySelector(qualitySelector)
                .build()
            val videoCapture = VideoCapture.withOutput(recorder)

            val selector = if (useFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, selector, videoCapture)
                isRecording = true
                broadcastState("Registrazione video in corso...")

                val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val prefix = if (useFrontCamera) "BE_VID_FRONT_" else "BE_VID_BACK_"
                val cv = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$prefix$ts.mp4")
                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/StealthCam")
                    }
                }

                val pendingRecording = videoCapture.output.prepareRecording(
                    this,
                    MediaStoreOutputOptions.Builder(
                        contentResolver,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    ).setContentValues(cv).build()
                )

                val hasAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                val configuredRecording = if (hasAudio) {
                    pendingRecording.withAudioEnabled()
                } else {
                    pendingRecording
                }

                recording = configuredRecording.start(
                    ContextCompat.getMainExecutor(this),
                    Consumer { event ->
                        when (event) {
                            is VideoRecordEvent.Start -> {
                                vibrate(doublePulse = false)
                            }
                            is VideoRecordEvent.Finalize -> {
                                isRecording = false
                                vibrate(doublePulse = true)
                                provider.unbindAll()
                                broadcastState("Video salvato in DCIM/StealthCam")
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                isRecording = false
                provider.unbindAll()
                broadcastState("Errore avvio video")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopVideo() {
        try {
            recording?.stop()
        } catch (_: Exception) {
        }
        recording = null
        isRecording = false
        broadcastState("Registrazione video interrotta e salvata")
    }

    private fun broadcastState(message: String) {
        val intent = Intent(ACTION_STATE_CHANGED).apply {
            setPackage(packageName)
            putExtra(EXTRA_STATUS_MESSAGE, message)
            putExtra(EXTRA_IS_RECORDING, isRecording)
        }
        sendBroadcast(intent)
    }

    private fun buildNotification(): Notification {
        val channelId = "blackeye_bg_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Servizio BlackEye",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                setShowBadge(false)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("BlackEye attivo in background")
            .setContentText("Tasti volume pronti all'uso")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()
    }

    private fun vibrate(doublePulse: Boolean) {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
        vibrator ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (doublePulse) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 70, 80, 70), -1))
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(80)
        }
    }

    override fun onDestroy() {
        isRunning = false
        stopVideo()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    companion object {
        private const val NOTIF_ID = 101
        const val ACTION_TAKE_PHOTO = "com.stealthcam.app.ACTION_TAKE_PHOTO"
        const val ACTION_START_VIDEO = "com.stealthcam.app.ACTION_START_VIDEO"
        const val ACTION_STOP_VIDEO = "com.stealthcam.app.ACTION_STOP_VIDEO"
        const val ACTION_TOGGLE_VIDEO = "com.stealthcam.app.ACTION_TOGGLE_VIDEO"
        const val ACTION_STATE_CHANGED = "com.stealthcam.app.ACTION_STATE_CHANGED"

        const val EXTRA_USE_FRONT_CAMERA = "extra_use_front_camera"
        const val EXTRA_STATUS_MESSAGE = "extra_status_message"
        const val EXTRA_IS_RECORDING = "extra_is_recording"

        @Volatile
        var isRecording: Boolean = false

        @Volatile
        var isRunning: Boolean = false
            private set
    }
}
