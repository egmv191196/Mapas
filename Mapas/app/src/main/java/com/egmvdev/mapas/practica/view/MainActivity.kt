package com.egmvdev.mapas.practica.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.egmvdev.mapas.R
import com.egmvdev.mapas.practica.model.AndroidContract
import com.egmvdev.mapas.databinding.ActivityMainBinding
import com.egmvdev.mapas.practica.model.Punto
import com.egmvdev.mapas.practica.model.PuntoLatLng
import com.egmvdev.mapas.practica.model.puntoModel
import com.egmvdev.mapas.practica.viewmodel.puntoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLngBounds




class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private  lateinit var bind: ActivityMainBinding
    private lateinit var mapa:GoogleMap
    private val puntoViewModel :puntoViewModel by viewModels()
    var bounds: LatLngBounds.Builder = LatLngBounds.Builder()
    val listadoPuntos = mutableListOf<PuntoLatLng>()
    val DbHelper = AndroidContract.AndroidDbHelper(this)//Adaptador para leer base de datos
    //val listaPuntos = mutableListOf<Punto>()//Arreglo para agregar los punto al leer la base de datos
    val listaPuntos = mutableListOf<puntoModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        crearFramentoMapa()

        bind.btnAdd.setOnClickListener {
            var intent: Intent = Intent(this, addPuntos::class.java)
            //intent.putExtra(resources.getString(R.string.identificador),"RECEPTOR 0 DESDE COMUNICACION")
            //startActivity(intent)
            resultadoLanzadorVM.launch(intent)
        }

        puntoViewModel.listaPuntos.observe(this, Observer{
            Log.i("prueba","Noto cambio")
            if(!it.isEmpty()){
                it.forEach {
                    mapa.addMarker(MarkerOptions()
                        .position(LatLng(it.longitud,it.latitud))
                        .title(it.nombre)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointmedio)))?.tag = it.nombre
                    bounds.include(LatLng(it.longitud,it.latitud))
                    Log.i("Puntos","${it.nombre}")
                    crearCirculo(LatLng(it.longitud,it.latitud))
                }
                try {
                    moverCamara(bounds.build().center)
                }catch (e: IllegalStateException){

                }
                crearPolilinea(it)
                }
        })
    }

    fun crearFramentoMapa(){
        val fragmentoMapa = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragmentoMapa.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        //crearMarcador()
        mapa.setOnMyLocationButtonClickListener(this)
        mapa.setOnMyLocationClickListener(this)
        moverCamara(LatLng(23.39964823928246, -102.37319850272843))
        //llenadoPuntos()


        //crearMarcadores()
        //habilitarUbicacion()
    }

    private fun crearMarcador(){
        val coord = LatLng(15.82896,-91.89840)
        val marcador = MarkerOptions()
            .position(coord)
            .title("Lagos de colon")
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.punto))

        mapa.addMarker(marcador)?.tag = 0
        mapa.setOnMarkerClickListener {
            val clicks = it.tag as Int
            it.tag = (clicks + 1)
            Toast.makeText(this,"Clics presionados $clicks", Toast.LENGTH_LONG).show()
            true
        }
        moverCamara(coord)
    }
    private fun moverCamara(coord: LatLng){
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(coord,6f))
    }
    private fun crearMarcadores(){
        listadoPuntos.add(PuntoLatLng(LatLng(19.432695204618003, -99.13156211319415), "ZOCALO DE LA CDMX","Aldahir",
            R.drawable.i2
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(19.346157924467136, -99.17746860223593), "Sport City","Fernando",
            R.drawable.gym
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(19.21518022039813, -98.73606894885947), "Parque Ecutiristico Dos Aguas","Dilan",
            R.drawable.i1
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(20.009284461048434, -97.5051591416877), "Cascada las golondrinas","Edgar",
            R.drawable.icono
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(24.33623,-110.31470), "Playa el Tecolote","Gabriel",
            R.drawable.playa
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(20.642257, -98.991836), "Grutas Tolantongo","Gerardo",
            R.drawable.i3
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(16.61070918258342, -99.12162460274021), "Pico del monte","Jair",
            R.drawable.beach
        ))
        listadoPuntos.add(PuntoLatLng(LatLng(15.782385510672311, -92.73105622766545), "las nuves chiapas","Erik",
            R.drawable.i4
        ))
        for (punto in listadoPuntos){
            mapa.addMarker(MarkerOptions()
                .position(punto.latLng)
                .title(punto.titulo)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(punto.rec)))?.tag = punto.titulo +" propiedad de " + punto.propietario
        }
        mapa.setOnMarkerClickListener(this)
        moverCamara(listadoPuntos[0].latLng)
        calcularMedio()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        //Toast.makeText(this,"Click en marcador ${p0.tag}",Toast.LENGTH_SHORT).show()
        return true
    }

    @SuppressLint("MissingPermission", "MissingSuperCall")
    private fun habilitarUbicacion(){
        if(!::mapa.isInitialized) return
        if(permisoHabilitado()) mapa.isMyLocationEnabled = true
        else solicitarPermisoUbicacion()

    }
    private fun permisoHabilitado() =
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun solicitarPermisoUbicacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this,"Es necesario activar permiso de ubicacion desde configuraciones",Toast.LENGTH_LONG).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),123)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mapa.isMyLocationEnabled = true
            }else{
                Toast.makeText(this,"Error al activar el permiso",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this,"Se presiono el boton de mi ubicacion",Toast.LENGTH_LONG).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this,"${p0.latitude} ${p0.longitude}",Toast.LENGTH_LONG).show()
    }
    fun calcularMedio(){
        for (punto in listadoPuntos.indices){
            Log.e("Pinicial","Punto inicial "+listadoPuntos[punto].titulo)
            for (punto1 in punto..listadoPuntos.size-1) {
                if (!listadoPuntos[punto].titulo.equals(listadoPuntos[punto1].titulo)) {
                    Log.e(
                        "Pmedio",
                        "Punto medio " + listadoPuntos[punto].titulo + "-" + listadoPuntos[punto1].titulo
                    )
                    var latitud =
                        (listadoPuntos[punto].latLng.latitude + listadoPuntos[punto1].latLng.latitude) / 2
                    var longitud =
                        (listadoPuntos[punto].latLng.longitude + listadoPuntos[punto1].latLng.longitude) / 2
                    var coordNuevas = LatLng(latitud, longitud)
                    mapa.addMarker(
                        MarkerOptions()
                            .position(coordNuevas)
                            .title("Punto medio " + listadoPuntos[punto].titulo + "-" + listadoPuntos[punto1].titulo)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointmedio))
                    )?.tag =
                        "Punto medio " + listadoPuntos[punto].titulo + "-" + listadoPuntos[punto1].titulo

                }
            }
        }
    }

    var resultadoLanzador =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
            if (resultado.resultCode == Activity.RESULT_OK){
                val nombre = resultado.data?.getStringExtra("Nombre")?:"Vacio"//Obtiene el mensaje del activity que fue llamado
                val longitud = resultado.data?.getStringExtra("Longitud")?:"Vacio"
                val latitud = resultado.data?.getStringExtra("Latitud")?:"Vacio"
                val db = DbHelper.writableDatabase
                val valores = ContentValues().apply {
                    put(AndroidContract.Punto.NOMBRE, nombre)
                    put(AndroidContract.Punto.LATITUD, longitud)
                    put(AndroidContract.Punto.LONGITUD, latitud)
                }
                val idAlumno = db.insert(AndroidContract.Punto.NOMBRE_TABLA, null,valores)
                //llenadoLibros()
                //var adapter = ListaLibroAdaptador(this, arreglo)
                //binding.LVLibros.adapter = adapter
                Toast.makeText(this, "Se agrego correctamente el lugar $idAlumno", Toast.LENGTH_LONG).show()
                //llenadoPuntos()
                    mapa.addMarker(MarkerOptions()
                    .position(LatLng(latitud.toDouble(),longitud.toDouble()))
                    .title(nombre)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointmedio)))?.tag = nombre
                crearCirculo(LatLng(latitud.toDouble(),longitud.toDouble()))
                llenadoPuntos()

            }
            else{
                Toast.makeText(this, "Error al agregar el lugar", Toast.LENGTH_SHORT).show()
            }
        }

    fun llenadoPuntos(){
        Log.i("LlenadoPuntos","Si entra a llenado")
        listaPuntos.clear()
        val db =DbHelper.readableDatabase
        val proyeccion = arrayOf(BaseColumns._ID,
            AndroidContract.Punto.NOMBRE, AndroidContract.Punto.LATITUD, AndroidContract.Punto.LONGITUD)
        val cursor = db.query(AndroidContract.Punto.NOMBRE_TABLA,proyeccion,null,null,null,null,null,null)
        with(cursor){
            //Log.i("Puntos","entra al cursor")
            while(moveToNext()){
                /*listaPuntos.add(
                    Punto(getLong(getColumnIndexOrThrow(BaseColumns._ID)),
                        getString(getColumnIndexOrThrow(AndroidContract.Punto.NOMBRE)),
                        getString(getColumnIndexOrThrow(AndroidContract.Punto.LATITUD)).toDouble(),
                        getString(getColumnIndexOrThrow(AndroidContract.Punto.LONGITUD)).toDouble())
                )*/
                //Log.i("Puntos","${getString(getColumnIndexOrThrow(AndroidContract.Punto.NOMBRE))}")
            }
        }
        //Log.i("Puntos","$listaPuntos")
        for (punto in listaPuntos){

            mapa.addMarker(MarkerOptions()
                .position(LatLng(punto.longitud,punto.latitud))
                .title(punto.nombre)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointmedio)))?.tag = punto.nombre
            bounds.include(LatLng(punto.longitud,punto.latitud))
            Log.i("Puntos","${punto.nombre}")
            crearCirculo(LatLng(punto.longitud,punto.latitud))
        }
        try {
            moverCamara(bounds.build().center)
        }catch (e: IllegalStateException){

        }
        crearPolilinea(listaPuntos)
        mapa.setOnMarkerClickListener(this)
    }
    /*fun crearPolilinea(listaPuntos: List<Punto>){
        val polilinea = PolylineOptions()
        for (punto in listaPuntos){
            polilinea.add(LatLng(punto.longitud,punto.latitud))
        }
        polilinea.startCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.start)))
        polilinea.clickable(true)
        mapa.addPolyline(polilinea)
    }*/
    fun crearPolilinea(listaPuntos: MutableList<puntoModel>){
        val polilinea = PolylineOptions()
        for (punto in listaPuntos){
            polilinea.add(LatLng(punto.longitud,punto.latitud))
        }
        polilinea.startCap(CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.start)))
        polilinea.clickable(true)
        mapa.addPolyline(polilinea)
    }
    fun crearCirculo(latLng: LatLng){
        val circulo = CircleOptions()
        circulo.center(latLng)
        circulo.radius(100.0)
        circulo.fillColor(ContextCompat.getColor(this, R.color.black))
        mapa.addCircle(circulo)
    }

    //funcion para ver si se recorre las lista del view model
    var resultadoLanzadorVM =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                resultado ->
            if (resultado.resultCode == Activity.RESULT_OK){
                val nombre = resultado.data?.getStringExtra("Nombre")?:"Vacio"//Obtiene el mensaje del activity que fue llamado
                val longitud = resultado.data?.getStringExtra("Longitud")?:"Vacio"
                val latitud = resultado.data?.getStringExtra("Latitud")?:"Vacio"
                puntoViewModel.agregarPunto(puntoModel(nombre, longitud.toDouble(),latitud.toDouble()))
                //llenadoLibros()
                //var adapter = ListaLibroAdaptador(this, arreglo)
                //binding.LVLibros.adapter = adapter
                Toast.makeText(this, "Se agrego correctamente el lugar $nombre", Toast.LENGTH_LONG).show()
                //llenadoPuntos()
                /*mapa.addMarker(MarkerOptions()
                    .position(LatLng(latitud.toDouble(),longitud.toDouble()))
                    .title(nombre)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointmedio)))?.tag = nombre
                crearCirculo(LatLng(latitud.toDouble(),longitud.toDouble()))
                llenadoPuntos()*/

            }
            else{
                Toast.makeText(this, "Error al agregar el lugar", Toast.LENGTH_SHORT).show()
            }
        }
}