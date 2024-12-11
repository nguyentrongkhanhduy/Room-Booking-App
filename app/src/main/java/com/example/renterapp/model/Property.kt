package com.example.renterapp.model

import com.google.firebase.firestore.DocumentId

class Property(
    @DocumentId
    var id: String = "",
    var location: Location = Location(),
    var price: Double = 0.0,
    var bedrooms: Int = 0,
    var imgUrl: String = "",
    var description: String = "",
    var isAvailable: Boolean = true,
    var ownerId: String = ""
) {
}