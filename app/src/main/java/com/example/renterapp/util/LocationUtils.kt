package com.example.renterapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.renterapp.R
import com.example.renterapp.model.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.type.LatLng

class LocationUtils(val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkGPSEnabled() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(context)

        val task = settingsClient.checkLocationSettings(locationSettingsRequest)
        task.addOnSuccessListener { }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(context as Activity, 1)
                } catch (e: Exception) {
                    Toast.makeText(context, "GPS is disabled", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(location: Location, onLocationFetched: (com.google.android.gms.maps.model.LatLng) -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastsLocation = locationResult.lastLocation
                if (lastsLocation != null) {
                    val tempLatLng =
                        com.google.android.gms.maps.model.LatLng(lastsLocation.latitude, lastsLocation.longitude)
                    location.updateLocation(lastsLocation.latitude, lastsLocation.longitude)
                    onLocationFetched(tempLatLng)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(location: Location, onLocationFetched: (com.google.android.gms.maps.model.LatLng) -> Unit) {
        if (hasLocationPermission(context)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val tempLatLng =
                        com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude)
                    location.updateLocation(it.latitude, it.longitude)
                    onLocationFetched(tempLatLng)
                } else {
                    Toast.makeText(context, "Fetching location...", Toast.LENGTH_LONG).show()
                    requestLocationUpdates(location, onLocationFetched)
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createCustomMarker(context: Context, labelText: String): BitmapDescriptor {
        val markerView = LayoutInflater.from(context).inflate(R.layout.custom_marker, null)

        val markerLabel = markerView.findViewById<TextView>(R.id.tvPriceMarker)
        markerLabel.text = labelText

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}