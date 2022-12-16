package com.example.bookingparking.user.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityLogoutUserBinding
import com.example.bookingparking.helper.SharedPreference
import com.example.bookingparking.helper.onSuccess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LogoutUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogoutUserBinding
    private lateinit var preference: SharedPreference
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()
        reference = Firebase.database.reference

        getProfileInfo()
        logout()
        navigation()
    }

    private fun getProfileInfo() {
        reference.child("user").child(auth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.apply {
                        tvPlateNumber.text = snapshot.child("plateNumber").value.toString()
                        tvName.text = snapshot.child("fullName").value.toString()
                        tvEmail.text = snapshot.child("email").value.toString()
                        tvType.text = preference.getAccountType()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }


    private fun logout() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            if (auth.currentUser == null) {
                startActivity(Intent(this@LogoutUserActivity, LoginUserActivity::class.java))
                finishAffinity()
                preference.clear()
                onSuccess("Logout Success")
            }
        }
    }

    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Profile User"
            setTitleTextColor(ContextCompat.getColor(this@LogoutUserActivity,
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