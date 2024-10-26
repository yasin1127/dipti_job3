package com.example.locationsharingapp_dipti_15.view15

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingapp_dipti_15.R
import com.example.locationsharingapp_dipti_15.databinding.ActivityMaps15Binding
import com.example.locationsharingapp_dipti_15.viewmodel15.FirestoreViewModel15
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity15 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMaps15Binding
    private lateinit var firestoreViewModel: FirestoreViewModel15
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMaps15Binding.inflate(layoutInflater)
        setContentView(binding.root)

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel15::class.java)

        // Initialize FusedLocationProviderClient for getting the user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map15) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Zoom in and Zoom out button functionality
        binding.btnZoomIn15.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }
        binding.btnZoomOut15.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check location permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Enable the 'My Location' layer to show the user's location
        mMap.isMyLocationEnabled = true

        // Get the current location and update the map
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } else {
                Log.w("MapsActivity", "Current location is null")
            }
        }

        firestoreViewModel.getAllUsers(this) { users ->
            for (user in users) {
                val userLocation = user.location
                Log.d("MapsActivity", "User: ${user.displayName}, Location: $userLocation")

                if (isValidLocation(userLocation)) {
                    val latLng = parseLocation(userLocation)
                    addMarker(latLng, user.displayName)
                } else {
                    Log.w("MapsActivity", "Invalid location for ${user.displayName}, using default")
                    val defaultLatLng = LatLng(37.4220936, -122.083922) // Google HQ as default
                    addMarker(defaultLatLng, "${user.displayName} (default location)")
                }
            }
        }
    }

    // Helper function to add marker
    private fun addMarker(latLng: LatLng, title: String) {
        val markerOptions = MarkerOptions().position(latLng).title(title)
        mMap.addMarker(markerOptions)
    }

    // Check if the location string is valid
    private fun isValidLocation(location: String?): Boolean {
        return location?.contains("Lat:") == true && location.contains("Long:")
    }

    // Parse the location string into LatLng
    private fun parseLocation(location: String): LatLng {
        val latLngSplit = location.split(", ")
        val latitude = latLngSplit[0].substringAfter("Lat: ").toDouble()
        val longitude = latLngSplit[1].substringAfter("Long: ").toDouble()
        return LatLng(latitude, longitude)
    }

    // Handle the result of location permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, reattempt getting the location
                onMapReady(mMap)
            } else {
                Log.e("MapsActivity", "Location permission denied")
            }
        }
    }
}
