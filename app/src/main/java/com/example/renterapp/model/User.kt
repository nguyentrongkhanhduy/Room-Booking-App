package com.example.renterapp.model

import com.google.firebase.firestore.DocumentId

class User(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val email: String = "",
) {
}