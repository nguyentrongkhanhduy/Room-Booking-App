package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.example.renterapp.databinding.ActivityWishlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WishlistActivity : BaseActivity() {
    private lateinit var binding: ActivityWishlistBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar!!.setTitle("Wish list")

        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun enableLoggedInUi() {
        binding.clNotLoggedIn.visibility = View.GONE

    }

    private fun disableLoggedInUi() {
        binding.clNotLoggedIn.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser != null){
            enableLoggedInUi()
        } else {
            disableLoggedInUi()
        }
        invalidateOptionsMenu()
    }
}