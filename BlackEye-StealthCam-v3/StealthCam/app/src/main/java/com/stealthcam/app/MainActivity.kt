package com.stealthcam.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnAction: Button
    private lateinit var txtStatus: TextView
    private lateinit var radioScreenOn: RadioButton
    private lateinit var radioScreenOff: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAction = findViewById(R.id.btnAction)
        txtStatus = findViewById(R.id.txtStatus)
        radioScreenOn = findViewById(R.id.radioScreenOn)
        radioScreenOff = findViewById(R.id.radioScreenOff)

        updateUIState()

        btnAction.setOnClickListener {
            if (CameraService.isRunning) {
                // Se è attivo, lo fermiamo
                stopService(Intent(this, CameraService::class.java))
                updateUIState()
            } else {
                // Se è spento, avviamo il servizio
                val serviceIntent = Intent(this, CameraService::class.java)
                startForegroundService(serviceIntent)
                
                if (radioScreenOn.isChecked) {
                    // Se è modalità schermo acceso, usciamo in background
                    updateUIState()
                    moveTaskToBack(true)
                } else {
                    // Altrimenti avviamo l'activity dello schermo nero
                    val blackScreenIntent = Intent(this, BlackScreenActivity::class.java)
                    startActivity(blackScreenIntent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUIState()
    }

    private fun updateUIState() {
        if (CameraService.isRunning) {
            btnAction.text = "FERMA SERVIZIO"
            btnAction.setBackgroundColor(getColor(android.R.color.holo_red_dark))
            txtStatus.text = "Stato servizio tasti rapidi: ATTIVO"
            txtStatus.setTextColor(getColor(android.R.color.holo_green_light))
        } else {
            btnAction.text = if (radioScreenOn.isChecked) "ATTIVA IN BACKGROUND (ESCI DALL'APP)" else "ATTIVA SCHERMO NERO"
            btnAction.setBackgroundColor(getColor(android.R.color.holo_red_light))
            txtStatus.text = "Stato servizio tasti rapidi: INATTIVO"
            txtStatus.setTextColor(getColor(android.R.color.darker_gray))
        }
    }
}
