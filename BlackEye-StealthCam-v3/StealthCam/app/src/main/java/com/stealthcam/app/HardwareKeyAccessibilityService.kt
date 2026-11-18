package com.stealthcam.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class HardwareKeyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_DPAD_UP -> {
                    dispatchCameraCommand(CameraService.ACTION_TAKE_PHOTO)
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    dispatchCameraCommand(CameraService.ACTION_TOGGLE_VIDEO)
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun dispatchCameraCommand(action: String) {
        val intent = Intent(this, CameraService::class.java).apply {
            this.action = action
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
