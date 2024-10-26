package com.example.locationsharingapp_dipti_15.view15

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingapp_dipti_15.databinding.ActivityLogin15Binding
import com.example.locationsharingapp_dipti_15.viewmodel15.AuthenticationViewModel15
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity15 : AppCompatActivity() {
    private lateinit var binding: ActivityLogin15Binding
    private lateinit var authenticationViewModel: AuthenticationViewModel15

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLogin15Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authenticationViewModel = ViewModelProvider(this).get(AuthenticationViewModel15::class.java)

        binding.loginBtn15.setOnClickListener {
            val email = binding.emailEt15.text.toString()
            val password = binding.passwordEt15.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show()
            } else {
                authenticationViewModel.login(email, password, {
                    startActivity(Intent(this, MainActivity15::class.java))
                    finish()
                }, {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                })
            }
        }

        binding.registerTxt15.setOnClickListener {
            startActivity(Intent(this, RegisterActivity15::class.java))
        }

        binding.forgotpass15.setOnClickListener {
            Toast.makeText(this, "Forgot password", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this@LoginActivity15, MainActivity15::class.java))
            finish()
        }
    }
}

