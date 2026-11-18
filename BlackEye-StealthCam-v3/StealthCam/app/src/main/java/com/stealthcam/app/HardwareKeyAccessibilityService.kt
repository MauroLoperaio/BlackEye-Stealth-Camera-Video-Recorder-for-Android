package com.stealthcam.app

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class HardwareKeyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Non serve leggere il contenuto delle finestre
    }

    override fun onInterrupt() {
        // Servizio interrotto dal sistema
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val prefs = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val isBgActive = CameraService.isRunning && prefs.getBoolean(MainActivity.KEY_BG_ACTIVE, false)

        // Intercetta i tasti solo quando BlackEye è stato attivato dall'utente (o sta registrando)
        if (!isBgActive && !CameraService.isRecording) {
            return super.onKeyEvent(event)
        }

        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_DPAD_UP -> {
                    val photoFront = prefs.getBoolean(MainActivity.KEY_PHOTO_FRONT, false)
                    dispatchCameraCommand(
                        action = CameraService.ACTION_TAKE_PHOTO,
                        useFrontCamera = photoFront
                    )
                    return true
                }

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val videoFront = prefs.getBoolean(MainActivity.KEY_VIDEO_FRONT, false)
                    dispatchCameraCommand(
                        action = CameraService.ACTION_TOGGLE_VIDEO,
                        useFrontCamera = videoFront
                    )
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun dispatchCameraCommand(action: String, useFrontCamera: Boolean) {
        val intent = Intent(this, CameraService::class.java).apply {
            this.action = action
            putExtra(CameraService.EXTRA_USE_FRONT_CAMERA, useFrontCamera)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
