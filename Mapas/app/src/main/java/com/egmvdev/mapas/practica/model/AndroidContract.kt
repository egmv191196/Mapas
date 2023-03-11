package com.egmvdev.mapas.practica.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object AndroidContract {
    //Alumno
    object Punto : BaseColumns {
        const val NOMBRE_TABLA = "puntos"
        const val NOMBRE = "nombre"
        const val LATITUD = "latitud"
        const val LONGITUD = "longitud"
    }

    private const val QUERY_CREAR_TABLA_PUNTO =
        "create table ${Punto.NOMBRE_TABLA} (${BaseColumns._ID} " +
                "integer primary key, ${Punto.NOMBRE} text," +
                "${Punto.LATITUD} text, ${Punto.LONGITUD} text)"

    private  const val QUERY_BORRAR_TABLA_ALUMNO = "drop table if exists ${Punto.NOMBRE_TABLA}"

    class AndroidDbHelper (context: Context) :
        SQLiteOpenHelper(context, NOMBRE_BASE_DE_DATOS,null, VERSION_BASE_DE_DATOS){
        companion object{
            const val VERSION_BASE_DE_DATOS = 1
            const val NOMBRE_BASE_DE_DATOS = "android"
        }

        override fun onCreate(p0: SQLiteDatabase?) {
            p0?.execSQL(QUERY_CREAR_TABLA_PUNTO)
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            //p0?.execSQL(QUERY_BORRAR_TABLA_ALUMNO)
        }
    }
}