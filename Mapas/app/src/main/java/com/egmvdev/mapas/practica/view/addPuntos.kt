package com.egmvdev.mapas.practica.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.egmvdev.mapas.databinding.ActivityAddPuntosBinding

class addPuntos : AppCompatActivity() {
    private lateinit var bind: ActivityAddPuntosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddPuntosBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.btnAddDatos.setOnClickListener {
            if(bind.etLat.text.isNotEmpty() && bind.etLon.text.isNotEmpty() && bind.etName.text.toString().isNotEmpty()){
                val intento = Intent()
                intento.putExtra("Nombre", bind.etName.text.toString())
                intento.putExtra("Latitud", bind.etLat.text.toString())
                intento.putExtra("Longitud", bind.etLon.text.toString())
                setResult(Activity.RESULT_OK,intento)//Envia un ok al activity que esta es esperando su respuesta
                finish()
            }
            else{
                setResult(Activity.RESULT_CANCELED)//Envio un canceled
                finish()
            }
        }
    }
}