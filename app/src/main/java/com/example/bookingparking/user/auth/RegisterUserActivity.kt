package com.example.bookingparking.user.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityRegisterUserBinding
import com.example.bookingparking.helper.*
import com.example.bookingparking.model.User
import com.example.bookingparking.user.parking.ParkingSlotActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var preference: SharedPreference
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()
        preference = SharedPreference(this)
        reference = Firebase.database.reference


        navigation()


        binding.loadingRegister.gone()
        binding.btnRegister.setOnClickListener {
            validationRegister()
        }


    }


    private fun validationRegister() {
        val fullName = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val plateNomor = binding.edtPlatNomor.text.toString().trim()

        when {
            fullName.isEmpty() -> {
                onError("Your name is required")
            }
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
            plateNomor.isEmpty() -> {
                onError("Plate number is required")
            }

            else -> {

                registerUser(fullName, email, password, plateNomor)

            }
        }
    }

    private fun registerUser(
        fullName: String,
        email: String,
        password: String,
        plateNomor: String,
    ) {
        binding.btnRegister.isEnabled = false
        binding.btnRegister.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.disable))
        binding.loadingRegister.visible()

        val user = User(fullName, email, plateNomor, "NO")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    reference.child("user")
                        .child(auth.currentUser?.uid!!)
                        .setValue(user)
                        .addOnCompleteListener { taskCreateUser ->
                            if (taskCreateUser.isSuccessful) {

                                preference.saveAccountType("USER")
                                preference.saveLoginState(true)

                                onSuccess("Register User Success")

                                // Change color of button
                                binding.btnRegister.isEnabled = true
                                binding.btnRegister.setBackgroundColor(ContextCompat.getColor(
                                    baseContext,
                                    R.color.blue_500))
                                binding.loadingRegister.gone()


                                // Move to ParkingSlotActivity
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(
                                        this,
                                        ParkingSlotActivity::class.java
                                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                                    finishAffinity()
                                }, 1500L)


                            }
                        }

                } else {
                    onError("Register Failed")
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.setBackgroundColor(ContextCompat.getColor(baseContext,
                        R.color.blue_500))
                    binding.loadingRegister.gone()

                }
            }
    }

    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Register Page User"
            setTitleTextColor(ContextCompat.getColor(this@RegisterUserActivity,
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