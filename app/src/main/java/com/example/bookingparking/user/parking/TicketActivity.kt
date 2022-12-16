package com.example.bookingparking.user.parking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityTicketBinding
import com.example.bookingparking.helper.SharedPreference
import com.example.bookingparking.helper.inVisible
import com.example.bookingparking.helper.visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding
    private lateinit var reference: DatabaseReference
    private lateinit var preference: SharedPreference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation()

        reference = Firebase.database.reference
        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()

        getDataTicket()

    }

    private fun getDataTicket() {
        binding.apply {
            loading.visible()
            containerCard.inVisible()
            ticketEmpty.inVisible()
            labelInformation.inVisible()
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000L)
            reference.child("user")
                .child(auth.uid.toString())
                .child("ticket")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            binding.apply {
                                containerCard.visible()
                                loading.inVisible()
                                ticketEmpty.inVisible()
                                labelInformation.visible()

                                // Set data ticket
                                labelCodeParking.text =
                                    snapshot.child("codeParking").value.toString()
                                labelName.text = snapshot.child("name").value.toString()
                                labelNumberPlate.text = snapshot.child("plate").value.toString()
                            }
                        } else {
                            binding.apply {
                                ticketEmpty.visible()
                                containerCard.inVisible()
                                loading.inVisible()
                                labelInformation.inVisible()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}

                })
        }
    }

    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "My Ticket Parking"
            setTitleTextColor(ContextCompat.getColor(this@TicketActivity,
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