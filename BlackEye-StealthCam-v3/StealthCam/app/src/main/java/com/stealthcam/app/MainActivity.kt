package com.stealthcam.app

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var rbPhoto: RadioButton
    private lateinit var rbVideo: RadioButton
    private lateinit var rbFrontCamera: RadioButton
    private lateinit var rbBackCamera: RadioButton
    private lateinit var rbScreenOn: RadioButton
    private lateinit var rbScreenOff: RadioButton
    private lateinit var btnStartAction: Button
    private lateinit var btnStopVideo: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvAccessibilityStatus: TextView
    private lateinit var btnEnableAccessibility: Button

    private val requiredPermissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val coreGranted = requiredPermissions
                .filter { it != Manifest.permission.POST_NOTIFICATIONS }
                .all { results[it] == true }

            if (coreGranted) {
                toggleServiceState()
            } else {
                Toast.makeText(
                    this,
                    "Permessi Fotocamera e Microfono necessari per funzionare.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra(CameraService.EXTRA_STATUS_MESSAGE) ?: return
            val recording = intent.getBooleanExtra(CameraService.EXTRA_IS_RECORDING, false)
            tvStatus.text = msg
            btnStopVideo.visibility = if (recording) View.VISIBLE else View.GONE
            updateButtonState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateAccessibilityUi()
        updateButtonState()
        btnStopVideo.visibility = if (CameraService.isRecording) View.VISIBLE else View.GONE

        val filter = IntentFilter(CameraService.ACTION_STATE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(stateReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(stateReceiver)
        } catch (_: Exception) {
        }
    }

    private fun bindViews() {
        rbPhoto = findViewById(R.id.rbPhoto)
        rbVideo = findViewById(R.id.rbVideo)
        rbFrontCamera = findViewById(R.id.rbFrontCamera)
        rbBackCamera = findViewById(R.id.rbBackCamera)
        rbScreenOn = findViewById(R.id.rbScreenOn)
        rbScreenOff = findViewById(R.id.rbScreenOff)
        btnStartAction = findViewById(R.id.btnStartAction)
        btnStopVideo = findViewById(R.id.btnStopVideo)
        tvStatus = findViewById(R.id.tvStatus)
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)
        btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility)
    }

    private fun setupListeners() {
        btnStartAction.setOnClickListener {
            if (!allCorePermissionsGranted()) {
                permissionsLauncher.launch(requiredPermissions)
                return@setOnClickListener
            }
            toggleServiceState()
        }

        btnStopVideo.setOnClickListener {
            sendCommandToService(CameraService.ACTION_STOP_VIDEO, rbFrontCamera.isChecked)
            btnStopVideo.visibility = View.GONE
        }

        btnEnableAccessibility.setOnClickListener {
            Toast.makeText(
                this,
                "Cerca 'BlackEye - Tasti Rapidi Fotocamera' e attivalo",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun toggleServiceState() {
        if (CameraService.isRunning) {
            // Se è già attivo, lo fermiamo completamente
            stopService(Intent(this, CameraService::class.java))
            tvStatus.text = "Servizio in background fermato."
            updateButtonState()
        } else {
            // Se è spento, lo avviamo
            val intent = Intent(this, CameraService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }

            if (rbScreenOff.isChecked) {
                startActivity(Intent(this, BlackScreenActivity::class.java))
            } else {
                tvStatus.text = "Servizio attivo in background. Premi i tasti volume ovunque."
            }
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        if (CameraService.isRunning) {
            btnStartAction.text = "FERMA SERVIZIO IN BACKGROUND"
            btnStartAction.setBackgroundColor(Color.parseColor("#37474F")) // Grigio scuro
        } else {
            btnStartAction.text = "AVVIA ACQUISIZIONE"
            btnStartAction.setBackgroundColor(Color.parseColor("#E53935")) // Rosso principale
        }
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

    private fun allCorePermissionsGranted(): Boolean {
        return requiredPermissions
            .filter { it != Manifest.permission.POST_NOTIFICATIONS }
            .all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun updateAccessibilityUi() {
        val enabled = isAccessibilityServiceEnabled()
        if (enabled) {
            tvAccessibilityStatus.text = "Stato servizio tasti rapidi: ATTIVO"
            tvAccessibilityStatus.setTextColor(Color.parseColor("#26A69A"))
            btnEnableAccessibility.text = "IMPOSTAZIONI TASTI RAPIDI (ATTIVO)"
        } else {
            tvAccessibilityStatus.text = "Stato servizio tasti rapidi: NON ATTIVO"
            tvAccessibilityStatus.setTextColor(Color.parseColor("#FF7043"))
            btnEnableAccessibility.text = "ATTIVA TASTI RAPIDI IN ACCESSIBILITÀ"
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponent = ComponentName(this, HardwareKeyAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)
        while (splitter.hasNext()) {
            val componentName = ComponentName.unflattenFromString(splitter.next())
            if (componentName != null && componentName == expectedComponent) {
                return true
            }
        }
        return false
    }
}
