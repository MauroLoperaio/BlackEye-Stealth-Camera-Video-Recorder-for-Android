package com.stealthcam.app

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var rgVideoCamera: RadioGroup
    private lateinit var rbVideoFront: RadioButton
    private lateinit var rbVideoBack: RadioButton
    private lateinit var rgPhotoCamera: RadioGroup
    private lateinit var rbPhotoFront: RadioButton
    private lateinit var rbPhotoBack: RadioButton
    private lateinit var rgScreenMode: RadioGroup
    private lateinit var rbScreenOn: RadioButton
    private lateinit var rbScreenOff: RadioButton
    private lateinit var btnMainAction: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvAccessibilityStatus: TextView
    private lateinit var btnEnableAccessibility: Button

    private val prefs by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

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
                handleMainButtonClick()
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
            tvStatus.text = msg
            updateMainButtonUi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        loadSavedPreferences()
        setupListeners()

        // Se l'app era stata interrotta forzatamente, riallinea lo stato reale del servizio
        if (!CameraService.isRunning) {
            prefs.edit().putBoolean(KEY_BG_ACTIVE, false).apply()
        }
        updateMainButtonUi()
    }

    override fun onResume() {
        super.onResume()
        if (!CameraService.isRunning) {
            prefs.edit().putBoolean(KEY_BG_ACTIVE, false).apply()
        }
        updateAccessibilityUi()
        updateMainButtonUi()

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
        rgVideoCamera = findViewById(R.id.rgVideoCamera)
        rbVideoFront = findViewById(R.id.rbVideoFront)
        rbVideoBack = findViewById(R.id.rbVideoBack)
        rgPhotoCamera = findViewById(R.id.rgPhotoCamera)
        rbPhotoFront = findViewById(R.id.rbPhotoFront)
        rbPhotoBack = findViewById(R.id.rbPhotoBack)
        rgScreenMode = findViewById(R.id.rgScreenMode)
        rbScreenOn = findViewById(R.id.rbScreenOn)
        rbScreenOff = findViewById(R.id.rbScreenOff)
        btnMainAction = findViewById(R.id.btnMainAction)
        tvStatus = findViewById(R.id.tvStatus)
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)
        btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility)
    }

    private fun loadSavedPreferences() {
        val videoFront = prefs.getBoolean(KEY_VIDEO_FRONT, false)
        val photoFront = prefs.getBoolean(KEY_PHOTO_FRONT, false)
        rbVideoFront.isChecked = videoFront
        rbVideoBack.isChecked = !videoFront
        rbPhotoFront.isChecked = photoFront
        rbPhotoBack.isChecked = !photoFront
    }

    private fun setupListeners() {
        rgVideoCamera.setOnCheckedChangeListener { _, checkedId ->
            prefs.edit().putBoolean(KEY_VIDEO_FRONT, checkedId == R.id.rbVideoFront).apply()
        }

        rgPhotoCamera.setOnCheckedChangeListener { _, checkedId ->
            prefs.edit().putBoolean(KEY_PHOTO_FRONT, checkedId == R.id.rbPhotoFront).apply()
        }

        rgScreenMode.setOnCheckedChangeListener { _, _ ->
            updateMainButtonUi()
        }

        btnMainAction.setOnClickListener {
            if (!allCorePermissionsGranted()) {
                permissionsLauncher.launch(requiredPermissions)
                return@setOnClickListener
            }
            handleMainButtonClick()
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

    private fun handleMainButtonClick() {
        val isBgActive = CameraService.isRunning && prefs.getBoolean(KEY_BG_ACTIVE, false)

        if (isBgActive) {
            // Secondo clic sullo stesso pulsante: disattiva completamente la modalità background
            prefs.edit().putBoolean(KEY_BG_ACTIVE, false).apply()
            stopService(Intent(this, CameraService::class.java))
            tvStatus.text = "Servizio in background: DISATTIVATO"
            tvStatus.setTextColor(Color.parseColor("#9E9EA7"))
            updateMainButtonUi()
            return
        }

        // Attivazione del servizio
        val serviceIntent = Intent(this, CameraService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        if (rbScreenOff.isChecked) {
            // Modalità Schermo Spento (uscita tramite triplo tocco)
            prefs.edit().putBoolean(KEY_BG_ACTIVE, true).apply()
            updateMainButtonUi()
            startActivity(Intent(this, BlackScreenActivity::class.java))
        } else {
            // Modalità Schermo Acceso: attiva in background ed esce dall'app
            prefs.edit().putBoolean(KEY_BG_ACTIVE, true).apply()
            tvStatus.text = "Servizio in background: ATTIVO (riapri l'app per fermarlo)"
            tvStatus.setTextColor(Color.parseColor("#26A69A"))
            updateMainButtonUi()
            moveTaskToBack(true)
        }
    }

    private fun updateMainButtonUi() {
        val isBgActive = CameraService.isRunning && prefs.getBoolean(KEY_BG_ACTIVE, false)
        if (isBgActive) {
            btnMainAction.text = "DISATTIVA BACKGROUND (FERMA)"
            btnMainAction.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#37474F"))
            tvStatus.text = "Servizio in background: ATTIVO"
            tvStatus.setTextColor(Color.parseColor("#26A69A"))
        } else {
            btnMainAction.text = if (rbScreenOff.isChecked) {
                "ATTIVA SCHERMO NERO"
            } else {
                "ATTIVA IN BACKGROUND (ESCI DALL'APP)"
            }
            btnMainAction.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E53935"))
            tvStatus.text = "Servizio in background: INATTIVO"
            tvStatus.setTextColor(Color.parseColor("#9E9EA7"))
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
            btnEnableAccessibility.text = "TASTI RAPIDI ATTIVI"
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

    companion object {
        const val PREFS_NAME = "blackeye_prefs"
        const val KEY_BG_ACTIVE = "bg_active"
        const val KEY_VIDEO_FRONT = "video_front"
        const val KEY_PHOTO_FRONT = "photo_front"
    }
}
