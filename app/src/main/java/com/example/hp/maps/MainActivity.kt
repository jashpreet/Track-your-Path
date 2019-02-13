package com.example.hp.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var locationCallback: LocationCallback

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            this.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            this.interval = 500
            this.fastestInterval = 500
        }
    }



    val RQ_LOCATION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RQ_LOCATION
            )
        }


    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .build()

            val settingClient = LocationServices.getSettingsClient(this)

            settingClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                }
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RQ_LOCATION)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


//         var marker : Marker = mMap.addMarker(MarkerOptions().position(currLocation))
        var prevLocation :Location?= null
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                val currLocation: Location? = locationResult.locations.get(0)

                currLocation?.let {
                    Log.e("TAG", "Lattitude is ${currLocation.latitude}")
                    Log.e("TAG", "Longitude is ${currLocation.longitude}")


                    var marker = mMap.addMarker(MarkerOptions().position(LatLng(currLocation.latitude , currLocation.longitude)).title("Marker"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(currLocation.latitude , currLocation.longitude)))


                    prevLocation?.let {
                        mMap.addPolyline(
                            PolylineOptions()
                                .add(LatLng(prevLocation!!.latitude , prevLocation!!.longitude), LatLng(currLocation.latitude , currLocation.longitude))
                                .color(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                                .width(5f)
                        )
                        marker.remove()
                    }


                }

                    prevLocation=currLocation
            }

            override fun onLocationAvailability(var1: LocationAvailability) {}

        }
    }
}



