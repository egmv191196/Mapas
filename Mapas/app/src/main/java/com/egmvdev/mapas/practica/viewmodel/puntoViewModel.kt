package com.egmvdev.mapas.practica.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.egmvdev.mapas.practica.model.puntoModel

class puntoViewModel: ViewModel() {
    val listaPuntos = MutableLiveData<MutableList<puntoModel>>()
    init {
        listaPuntos.value = ArrayList()
    }
    fun agregarPunto(punto : puntoModel){
        listaPuntos.value?.add(punto)
        listaPuntos.value =listaPuntos.value
    }
}