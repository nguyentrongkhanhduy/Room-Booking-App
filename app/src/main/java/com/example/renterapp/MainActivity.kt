package com.example.renterapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.renterapp.databinding.ActivityMainBinding
import com.example.renterapp.model.Location
import com.example.renterapp.model.User
import com.example.renterapp.util.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var locationUtils: LocationUtils
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        locationUtils = LocationUtils(this)
        location = Location()

        val checkLocationPermission = locationUtils.hasLocationPermission(this)
        if (!checkLocationPermission) {
            requestLocationPermission()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.fragMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                locationUtils.checkGPSEnabled()
            } else {
                // Permission denied
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (!rationalRequired) {
                    // User has permanently denied the permission
                    Toast.makeText(
                        this,
                        "Location permission is required to use this app, please enable it in the settings",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    // Permission denied, but not permanently denied
                    Toast.makeText(
                        this,
                        "Location permission is required to use this app",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        setAppBarTitle()
    }

    override fun setAppBarTitle() {
        if (auth.currentUser == null) {
            supportActionBar!!.setTitle("Welcome to Renter App!")
        } else {
            this.userId = auth.currentUser!!.uid
            db.collection("user").document(this.userId).get().addOnSuccessListener {
                this.user = it.toObject(User::class.java)
                if (this.user != null) {
                    supportActionBar!!.setTitle("Welcome back, ${this.user!!.username}!")
                } else {
                    supportActionBar!!.setTitle("Welcome to Renter App!")
                }
            }
        }
        invalidateOptionsMenu()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.678684, -79.372565), 12f)) //Toronto
        }

    }

}