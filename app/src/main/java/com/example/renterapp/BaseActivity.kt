package com.example.renterapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        supportActionBar!!.setTitle("")
        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#fff8ff"))
        )

        val check = this is MainActivity
        supportActionBar!!.setDisplayHomeAsUpEnabled(!check)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar_item, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(auth.currentUser == null){
            menu?.findItem(R.id.miLogin)?.isVisible = true
            menu?.findItem(R.id.miLogout)?.isVisible = false
        } else {
            menu?.findItem(R.id.miLogin)?.isVisible = false
            menu?.findItem(R.id.miLogout)?.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
            R.id.miWishList -> {
                if(this is WishlistActivity) return true

                Intent(this, WishlistActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.miLogin -> {
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.miLogout -> {

            }
        }
        return true
    }
}