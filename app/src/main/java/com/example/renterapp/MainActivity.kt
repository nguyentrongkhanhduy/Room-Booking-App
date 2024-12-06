package com.example.renterapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.renterapp.databinding.ActivityMainBinding
import com.example.renterapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
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

}