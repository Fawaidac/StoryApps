package com.polije.storyapps.view

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.polije.storyapps.model.Story
import com.polije.storyapps.view.viewmodel.MapsViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.polije.storyapps.R


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMaps: GoogleMap
    private val FINE_PERMISSION_CODE = 1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mapViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel = ViewModelProvider(this)[MapsViewModel::class.java]
    }

    override fun onMapReady(p0: GoogleMap) {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        gMaps = p0
        gMaps.uiSettings.isZoomControlsEnabled = true
        gMaps.uiSettings.isIndoorLevelPickerEnabled = true
        gMaps.uiSettings.isCompassEnabled = true
        gMaps.uiSettings.isMapToolbarEnabled = true
        checkLocationPermission()
        if (token != null) {
            mapViewModel.fetchStories(token)
        }

        mapViewModel.stories.observe(this, Observer { stories ->
            displayMarkers(stories)
        })

        mapViewModel.error.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE)
            return
        }
        gMaps.isMyLocationEnabled = true
    }

    private fun displayMarkers(stories: List<Story>) {
        val boundsBuilder = LatLngBounds.Builder()
        for (story in stories) {
            val latLng = LatLng(story.lat, story.lon)
            gMaps.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(story.description))
            boundsBuilder.include(latLng)
        }
        val bounds = boundsBuilder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds,  width, height, padding)
        gMaps.animateCamera(cu)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission()
            } else {
                Toast.makeText(this, "Location Permission is Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}