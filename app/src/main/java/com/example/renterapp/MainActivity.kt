package com.example.renterapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.renterapp.databinding.ActivityMainBinding
import com.example.renterapp.model.Location
import com.example.renterapp.model.Property
import com.example.renterapp.model.User
import com.example.renterapp.util.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Locale

class MainActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var locationUtils: LocationUtils
    private lateinit var googleMap: GoogleMap
    private lateinit var location: Location
    private lateinit var geoCoder: Geocoder

    val propertyListDisplay = mutableListOf<Property>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        locationUtils = LocationUtils(this)
        location = Location()
        geoCoder = Geocoder(this, Locale.getDefault())

        val checkLocationPermission = locationUtils.hasLocationPermission(this)
        if (!checkLocationPermission) {
            requestLocationPermission()
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.fragMap) as SupportMapFragment
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

            locationUtils.fetchCurrentLocation(location) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 11f))
            }

            getAllProperty()

            googleMap.setOnMarkerClickListener {
                showProperty(it.tag as Int)
                true
            }
        }
    }

    private fun getAllProperty() {
        googleMap.clear()
        propertyListDisplay.clear()
        db.collection("property").get().addOnSuccessListener {
            for (document in it) {
                try {
                    val property = document.toObject(Property::class.java)
                    propertyListDisplay.add(property)
                    val latLngProperty =
                        LatLng(property.location.latitude, property.location.longitude)
                    val marker = googleMap.addMarker(
                        MarkerOptions().position(latLngProperty)
                            .icon(locationUtils.createCustomMarker(this, "$${property.price} CAD"))
                    )
                    marker!!.tag = propertyListDisplay.indexOf(property)
                } catch (e: Exception) {
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.d("Debug", e.localizedMessage)
                }
            }
        }
    }

    private fun showProperty(position: Int) {
        Toast.makeText(this, propertyListDisplay.get(position).description, Toast.LENGTH_SHORT)
            .show()
        val customDialog = LayoutInflater.from(this).inflate(R.layout.property_detail_layout, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(customDialog)
            .create()

        val address = customDialog.findViewById<TextView>(R.id.tvAddress)
        val description = customDialog.findViewById<TextView>(R.id.tvDescription)
        val price = customDialog.findViewById<TextView>(R.id.tvPrice)
        val image = customDialog.findViewById<ImageView>(R.id.ivRoom)


        val searchResult = geoCoder.getFromLocation(
            propertyListDisplay.get(position).location.latitude,
            propertyListDisplay.get(position).location.longitude,
            1
        )
        if (searchResult != null && searchResult.isNotEmpty()) {
            address.text = searchResult[0].getAddressLine(0)
        }
        description.text = propertyListDisplay.get(position).description
        price.text = "$${propertyListDisplay.get(position).price} CAD"

        val btnDismiss = customDialog.findViewById<ImageButton>(R.id.btnClose)
        btnDismiss.setOnClickListener {
            alertDialog.dismiss()
        }



        alertDialog.show()
    }
}