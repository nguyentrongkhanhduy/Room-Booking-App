package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import com.example.renterapp.databinding.ActivityWishlistBinding

class WishlistActivity : BaseActivity() {
    private lateinit var binding: ActivityWishlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setTitle("Wish list")

        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}