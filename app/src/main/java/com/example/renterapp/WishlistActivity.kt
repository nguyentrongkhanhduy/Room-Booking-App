package com.example.renterapp

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.renterapp.adapter.ClickInterface
import com.example.renterapp.adapter.PropertyAdapter
import com.example.renterapp.databinding.ActivityWishlistBinding
import com.example.renterapp.model.Property
import com.example.renterapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WishlistActivity : BaseActivity(), ClickInterface {
    private lateinit var binding: ActivityWishlistBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var geoCoder: Geocoder

    val propertyListDisplay = mutableListOf<Property>()
    val wishList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar!!.setTitle("Your Wishlist")

        geoCoder = Geocoder(this)
        val adapter = PropertyAdapter(propertyListDisplay, geoCoder, this)
        binding.rvProperty.adapter = adapter
        binding.rvProperty.layoutManager = LinearLayoutManager(this)

        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun enableLoggedInUi() {
        binding.clNotLoggedIn.visibility = View.GONE
        binding.layoutWishList.visibility = View.VISIBLE
    }

    private fun disableLoggedInUi() {
        binding.clNotLoggedIn.visibility = View.VISIBLE
        binding.layoutWishList.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            enableLoggedInUi()
            getCurrentUserInfo()
        } else {
            disableLoggedInUi()
        }
        invalidateOptionsMenu()
    }

    private fun getCurrentUserInfo() {
        if (auth.currentUser != null) {
            db.collection("user").document(auth.currentUser!!.uid).get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    wishList.clear()
                    wishList.addAll(user.wishList)
                }

                propertyListDisplay.clear()
                for(id in wishList){
                    db.collection("property").document(id).get().addOnSuccessListener {
                        val property = it.toObject(Property::class.java)
                        if(property!=null){
                            propertyListDisplay.add(property)
                            binding.rvProperty.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun removeFromWishlist(propertyId: String, position: Int) {
        wishList.remove(propertyId)
        db.collection("user").document(auth.currentUser!!.uid).update("wishList", wishList)
        propertyListDisplay.removeAt(position)
        binding.rvProperty.adapter?.notifyItemRemoved(position)
        binding.rvProperty.adapter?.notifyItemRangeChanged(position, propertyListDisplay.size)
        Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
    }
}