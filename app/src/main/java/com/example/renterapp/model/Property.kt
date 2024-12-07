package com.example.renterapp.model

import com.google.firebase.firestore.DocumentId

class Property(
    @DocumentId
    val id: String = "",
    val location: Location = Location(),
    val price: Double = 0.0,
    val imgUlr: String = "",
//    val description: String,
) {
}