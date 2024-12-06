package com.example.renterapp.model

import com.google.firebase.firestore.DocumentId

class Property(
    @DocumentId
    val id: String = "",
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    val imgUlr: String,
//    val description: String,
) {
}