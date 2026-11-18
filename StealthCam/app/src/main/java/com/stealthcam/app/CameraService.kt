package com.stealthcam.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleService
import java.text.SimpleDateFormat
import java.util.*

class CameraService : LifecycleService() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var recording: Recording? = null
    private var isRecording = false

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIF_ID, buildNotification())
        createInvisibleOverlay()
    }

    private fun createInvisibleOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        overlayView = object : View(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (event.keyCode) {
                        KeyEvent.KEYCODE_VOLUME_UP   -> { takePhoto(); return true }
                        KeyEvent.KEYCODE_VOLUME_DOWN -> { if (isRecording) stopVideo() else startVideo(); return true }
                    }
                }
                return super.dispatchKeyEvent(event)
            }
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            1, 1, type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        try {
            windowManager?.addView(overlayView, params)
            overlayView?.isFocusable = true
            overlayView?.isFocusableInTouchMode = true
            overlayView?.requestFocus()
        } catch (_: Exception) {}
    }

    // FOTO — fotocamera FRONTALE
    private fun takePhoto() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, capture)

                val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val cv = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "SC_$ts.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/StealthCam")
                }

                capture.takePicture(
                    ImageCapture.OutputFileOptions.Builder(
                        contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv
                    ).build(),
                    ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(o: ImageCapture.OutputFileResults) {
                            vibrate()
                            provider.unbindAll()
                        }
                        override fun onError(e: ImageCaptureException) {
                            provider.unbindAll()
                        }
                    }
                )
            } catch (e: Exception) { provider.unbindAll() }
        }, ContextCompat.getMainExecutor(this))
    }

    // VIDEO — fotocamera POSTERIORE
    private fun startVideo() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val vc = VideoCapture.withOutput(
                Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
            )
            try {
                provider.unbindAll()
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, vc)
                isRecording = true

                val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val cv = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "SC_Video_$ts.mp4")
                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/StealthCam")
                }

                recording = vc.output
                    .prepareRecording(this,
                        MediaStoreOutputOptions.Builder(
                            contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        ).setContentValues(cv).build()
                    )
                    .withAudioEnabled()
                    .start(ContextCompat.getMainExecutor(this), Consumer { event ->
                        when (event) {
                            is VideoRecordEvent.Start    -> vibrate()
                            is VideoRecordEvent.Finalize -> {
                                isRecording = false
                                vibrate()
                                provider.unbindAll()
                            }
                        }
                    })
            } catch (e: Exception) { isRecording = false; provider.unbindAll() }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopVideo() { recording?.stop(); recording = null }

    private fun buildNotification(): Notification {
        val ch = "sc_bg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(ch, "SC", NotificationManager.IMPORTANCE_MIN).apply {
                setShowBadge(false)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, ch)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle(" ").setContentText(" ")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java)?.defaultVibrator
                ?.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
                else
                    @Suppress("DEPRECATION") v.vibrate(80)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVideo()
        try { overlayView?.let { windowManager?.removeView(it) } } catch (_: Exception) {}
    }

    override fun onBind(intent: Intent): IBinder? { super.onBind(intent); return null }

    companion object { private const val NOTIF_ID = 1 }
}
