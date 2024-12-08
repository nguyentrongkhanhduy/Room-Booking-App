package com.example.renterapp.model

class Location(var latitude: Double = 0.0, var longitude: Double = 0.0) {
    fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}