package com.example.renterapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.renterapp.databinding.ActivitySignupBinding
import com.example.renterapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    val db = Firebase.firestore.collection("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar!!.hide()
        binding.tvError.visibility = View.INVISIBLE

        binding.btnBack.setOnClickListener {
            finish()
        }

        checkAllFields()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkAllFields()
            }

            override fun afterTextChanged(s: Editable?) {
                checkAllFields()
            }
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
        binding.etPasswordConfirm.addTextChangedListener(textWatcher)
        binding.etUsername.addTextChangedListener(textWatcher)

        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordConfirm = binding.etPasswordConfirm.text.toString()
            val username = binding.etUsername.text.toString()

            if(!checkPassword(password, passwordConfirm)) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Passwords do not match!"
                return@setOnClickListener
            } else {
                signUp(email, password, username)
            }

        }
    }

    private fun disableButton() {
        binding.btnSignup.setBackgroundResource(R.drawable.rounded_corner_button_disabled)
        binding.btnSignup.isEnabled = false
    }

    private fun enableButton() {
        binding.btnSignup.setBackgroundResource(R.drawable.rounded_corner_button)
        binding.btnSignup.isEnabled = true
    }

    private fun checkAllFields() {
        val check = binding.etEmail.text.isNotEmpty()
                && binding.etPassword.text.isNotEmpty()
                && binding.etPasswordConfirm.text.isNotEmpty()
                && binding.etUsername.text.isNotEmpty()

        if (check) {
            enableButton()
        } else {
            disableButton()
        }
    }

    private fun checkPassword(password: String, passwordConfirm: String):Boolean {
        return password == passwordConfirm
    }

    private fun signUp(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val user = auth.currentUser
            val userToAdd = User(user!!.uid, username, email)
            db.document(user.uid).set(userToAdd)

            finish()
        }.addOnFailureListener {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = it.localizedMessage
        }
    }

}