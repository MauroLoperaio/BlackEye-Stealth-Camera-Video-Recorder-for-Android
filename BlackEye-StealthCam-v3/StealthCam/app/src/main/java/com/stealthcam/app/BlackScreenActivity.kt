package com.stealthcam.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class BlackScreenActivity : AppCompatActivity() {

    private var tapCount = 0
    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantieni schermo attivo, luminosità al minimo assoluto e copri anche il foro fotocamera
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val lp = window.attributes
        lp.screenBrightness = 0.0f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = lp

        setContentView(R.layout.activity_black_screen)
        hideSystemBars()

        // Impedisci che gesti Indietro accidentali chiudano lo schermo nero
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Resta nero finché l'utente non esegue un triplo tocco
            }
        })

        // Solo un triplo tocco intenzionale esce dallo schermo nero
        findViewById<View>(R.id.blackRoot).setOnClickListener {
            val now = SystemClock.elapsedRealtime()
            if (now - lastTapTime < 550L) {
                tapCount++
            } else {
                tapCount = 1
            }
            lastTapTime = now

            if (tapCount >= 3) {
                tapCount = 0
                exitBlackScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.repeatCount == 0) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP,
                KeyEvent.KEYCODE_DPAD_UP -> {
                    sendCommandToService(CameraService.ACTION_TAKE_PHOTO)
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    // Avvia o ferma il video mantenendo lo schermo completamente nero
                    sendCommandToService(CameraService.ACTION_TOGGLE_VIDEO)
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitBlackScreen() {
        if (CameraService.isRecording) {
            sendCommandToService(CameraService.ACTION_STOP_VIDEO)
        }
        finish()
    }

    private fun sendCommandToService(action: String) {
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
