package com.stealthcam.app

import android.accessibilityservice.AccessibilityService
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
        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            when (event.keyCode) {
                // "Freccia su" / Volume Su -> Foto fotocamera frontale ovunque
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_DPAD_UP -> {
                    dispatchCameraCommand(
                        action = CameraService.ACTION_TAKE_PHOTO,
                        useFrontCamera = true
                    )
                    return true
                }

                // Volume Giù -> Avvia/Ferma video fotocamera posteriore ovunque
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    dispatchCameraCommand(
                        action = CameraService.ACTION_TOGGLE_VIDEO,
                        useFrontCamera = false
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
