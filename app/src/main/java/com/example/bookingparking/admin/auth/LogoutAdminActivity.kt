package com.example.bookingparking.admin.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityLogoutAdminBinding
import com.example.bookingparking.helper.SharedPreference
import com.example.bookingparking.helper.onSuccess
import com.example.bookingparking.user.auth.LoginUserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LogoutAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogoutAdminBinding
    private lateinit var preference: SharedPreference
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()
        reference = Firebase.database.reference

        navigation()
        getProfile()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            if (auth.currentUser == null){
                startActivity(Intent(this@LogoutAdminActivity, LoginUserActivity::class.java))
                finishAffinity()
                preference.clear()
                onSuccess("Logout Success")
            }
        }

    }

    private fun getProfile() {
        val uid = auth.uid.toString()
        Log.d("UID", uid)
        reference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                binding.apply {
                    tvName.text = snapshot.child("name").value.toString()
                    tvEmail.text = snapshot.child("email").value.toString()
                    tvType.text = snapshot.child("as").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Profile Info"
            setTitleTextColor(ContextCompat.getColor(this@LogoutAdminActivity,
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