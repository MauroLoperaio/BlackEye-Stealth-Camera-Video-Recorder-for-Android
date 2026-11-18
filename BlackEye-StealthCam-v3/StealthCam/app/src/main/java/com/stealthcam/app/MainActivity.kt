package com.stealthcam.app

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
    private lateinit var tvAccessibilityStatus: TextView
    private lateinit var btnEnableAccessibility: Button

    private val requiredPermissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val coreGranted = requiredPermissions.all { results[it] == true }
            if (coreGranted) {
                startBackgroundService()
            } else {
                Toast.makeText(
                    this,
                    "Permessi Fotocamera e Microfono necessari.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        loadSavedPreferences()
        setupListeners()

        if (!allCorePermissionsGranted()) {
            permissionsLauncher.launch(requiredPermissions)
        } else {
            startBackgroundService()
        }
    }

    override fun onResume() {
        super.onResume()
        updateAccessibilityUi()
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
        tvAccessibilityStatus = findViewById(R.id.tvAccessibilityStatus)
        btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility)
    }

    private fun loadSavedPreferences() {
        val prefs = getSharedPreferences(CameraService.PREFS_NAME, Context.MODE_PRIVATE)
        val videoFront = prefs.getBoolean(CameraService.PREF_VIDEO_FRONT, false)
        val photoFront = prefs.getBoolean(CameraService.PREF_PHOTO_FRONT, true)

        if (videoFront) rbVideoFront.isChecked = true else rbVideoBack.isChecked = true
        if (photoFront) rbPhotoFront.isChecked = true else rbPhotoBack.isChecked = true
        updateMainButtonLabel()
    }

    private fun saveCameraPreferences() {
        val prefs = getSharedPreferences(CameraService.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(CameraService.PREF_VIDEO_FRONT, rbVideoFront.isChecked)
            .putBoolean(CameraService.PREF_PHOTO_FRONT, rbPhotoFront.isChecked)
            .apply()
    }

    private fun setupListeners() {
        rgVideoCamera.setOnCheckedChangeListener { _, _ ->
            saveCameraPreferences()
        }

        rgPhotoCamera.setOnCheckedChangeListener { _, _ ->
            saveCameraPreferences()
        }

        rgScreenMode.setOnCheckedChangeListener { _, _ ->
            updateMainButtonLabel()
        }

        btnMainAction.setOnClickListener {
            if (!allCorePermissionsGranted()) {
                permissionsLauncher.launch(requiredPermissions)
                return@setOnClickListener
            }

            saveCameraPreferences()
            startBackgroundService()

            if (rbScreenOff.isChecked) {
                startActivity(Intent(this, BlackScreenActivity::class.java))
            } else {
                moveTaskToBack(true)
            }
        }

        btnEnableAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun updateMainButtonLabel() {
        btnMainAction.text = if (rbScreenOff.isChecked) {
            "ATTIVA SCHERMO NERO"
        } else {
            "ATTIVA IN BACKGROUND (ESCI DALL'APP)"
        }
    }

    private fun startBackgroundService() {
        val intent = Intent(this, CameraService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun allCorePermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
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
}
