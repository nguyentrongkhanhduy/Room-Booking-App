package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.renterapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        supportActionBar!!.hide()
        binding.tvError.visibility = View.INVISIBLE

        checkAllFields()

        val textWatcher = object:TextWatcher{
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

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            login(email, password)
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            finish()
        }
    }

    private fun disableButton() {
        binding.btnLogin.setBackgroundResource(R.drawable.rounded_corner_button_disabled)
        binding.btnLogin.isEnabled = false
    }

    private fun enableButton() {
        binding.btnLogin.setBackgroundResource(R.drawable.rounded_corner_button)
        binding.btnLogin.isEnabled = true
    }

    private fun checkAllFields() {
        val check = binding.etEmail.text.isNotEmpty()
                && binding.etPassword.text.isNotEmpty()

        if (check) {
            enableButton()
        } else {
            disableButton()
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            finish()
        }.addOnFailureListener {
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = "The email or password you entered is incorrect!"
        }
    }
}