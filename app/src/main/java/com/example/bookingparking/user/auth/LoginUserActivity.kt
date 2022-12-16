package com.example.bookingparking.user.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.admin.auth.LoginAdminActivity
import com.example.bookingparking.databinding.ActivityLoginUserBinding
import com.example.bookingparking.helper.*
import com.example.bookingparking.user.parking.ParkingSlotActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()
        databaseReference = Firebase.database.reference

        navigation()


        binding.loadingLogin.gone()
        binding.btnLogin.setOnClickListener {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.setBackgroundColor(ContextCompat.getColor(baseContext,
                R.color.disable))
            binding.loadingLogin.visible()
            loginValidation()
        }

    }

    private fun loginValidation() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
               onError("Email is required")
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                onError("Invalid email format")
            }
            password.isEmpty() -> {
                onError("Password is required")
            }
            password.length < 8 -> {
                onError("Password at least 8 character")
            }
            else -> {

                requestsLogin(email, password)

            }
        }
    }

    private fun requestsLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                   onSuccess("Login Success")

                    preference.saveLoginState(true)
                    preference.saveAccountType("USER")
                    preference.saveUserID(auth.currentUser?.uid.toString())


                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.setBackgroundColor(ContextCompat.getColor(
                        baseContext,
                        R.color.blue_500))
                    binding.loadingLogin.gone()


                    Handler(mainLooper).postDelayed({
                        startActivity(Intent(this@LoginUserActivity,
                            ParkingSlotActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finishAffinity()
                    }, 1000L)

                } else {
                    onError("Login Failure")
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.setBackgroundColor(ContextCompat.getColor(
                        baseContext,
                        R.color.blue_500))
                    binding.loadingLogin.gone()
                }
            }
    }

    private fun navigation() {
        binding.btnToLoginAdmin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }
        binding.btnToRegisterUser.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
        }
    }
}