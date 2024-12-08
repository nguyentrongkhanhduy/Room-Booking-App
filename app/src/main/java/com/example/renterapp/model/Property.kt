package com.example.renterapp.model

import com.google.firebase.firestore.DocumentId

class Property(
    @DocumentId
    var id: String = "",
    var location: Location = Location(),
    var price: Double = 0.0,
    var imgUlr: String = "",
    var description: String = "",
) {
}