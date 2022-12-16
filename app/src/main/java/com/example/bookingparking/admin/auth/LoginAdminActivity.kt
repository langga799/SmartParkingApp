package com.example.bookingparking.admin.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.admin.dashboard.ParkingDashboardActivity
import com.example.bookingparking.databinding.ActivityLoginAdminBinding
import com.example.bookingparking.helper.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var preferences: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = SharedPreference(this)
        auth = FirebaseAuth.getInstance()
        reference = Firebase.database.reference


        navigation()


        binding.loadingLogin.gone()
        binding.btnLogin.setOnClickListener {
            validateLogin()
        }


    }

    private fun validateLogin() {
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
        binding.btnLogin.isEnabled = false
        binding.btnLogin.setBackgroundColor(ContextCompat.getColor(baseContext,
            R.color.disable))
        binding.loadingLogin.visible()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onSuccess("Login Success")

                    preferences.saveLoginState(true)
                    preferences.saveAccountType("ADMIN")
                    preferences.saveUserID(auth.currentUser?.uid.toString())


                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.setBackgroundColor(ContextCompat.getColor(
                        baseContext,
                        R.color.blue_500))
                    binding.loadingLogin.gone()


                    Handler(mainLooper).postDelayed({
                        startActivity(Intent(
                            this@LoginAdminActivity,
                            ParkingDashboardActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        finishAffinity()
                    }, 1000L)


                } else {
                    Log.d("error", task.exception?.message.toString())
                    onError("Login Error")

                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.setBackgroundColor(ContextCompat.getColor(
                        baseContext,
                        R.color.blue_500))
                    binding.loadingLogin.gone()
                }


            }
    }


    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Login Page"
            setTitleTextColor(ContextCompat.getColor(this@LoginAdminActivity,
                R.color.white))
            navigationIcon =
                ContextCompat.getDrawable(baseContext, R.drawable.ic_round_arrow_back_24)
        }.apply {
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}