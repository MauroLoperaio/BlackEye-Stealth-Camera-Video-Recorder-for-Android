package com.stealthcam.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class BlackScreenActivity : AppCompatActivity() {

    private var tapCount = 0
    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantieni sveglio il dispositivo e porta la luminosità al minimo assoluto
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val lp = window.attributes
        lp.screenBrightness = 0.01f
        window.attributes = lp

        setContentView(R.layout.activity_black_screen)
        hideSystemBars()

        // Triplo tocco sullo schermo nero per uscire e tornare ai controlli
        findViewById<View>(R.id.blackRoot).setOnClickListener {
            val now = SystemClock.elapsedRealtime()
            if (now - lastTapTime < 600L) {
                tapCount++
            } else {
                tapCount = 1
            }
            lastTapTime = now

            if (tapCount >= 3) {
                exitBlackScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_DPAD_UP -> {
                sendCommandToService(CameraService.ACTION_TAKE_PHOTO, useFront = true)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (CameraService.isRecording) {
                    sendCommandToService(CameraService.ACTION_STOP_VIDEO, useFront = false)
                    exitBlackScreen()
                } else {
                    sendCommandToService(CameraService.ACTION_START_VIDEO, useFront = false)
                }
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                exitBlackScreen()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitBlackScreen() {
        if (CameraService.isRecording) {
            sendCommandToService(CameraService.ACTION_STOP_VIDEO, useFront = false)
        }
        finish()
    }

    private fun sendCommandToService(action: String, useFront: Boolean) {
        val intent = Intent(this, CameraService::class.java).apply {
            this.action = action
            putExtra(CameraService.EXTRA_USE_FRONT_CAMERA, useFront)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
