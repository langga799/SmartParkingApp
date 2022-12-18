package com.example.bookingparking.admin.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.admin.auth.LogoutAdminActivity
import com.example.bookingparking.databinding.ActivityParkingDashboardBinding
import com.example.bookingparking.helper.*
import com.example.bookingparking.model.ParkingTicket
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ParkingDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkingDashboardBinding
    private lateinit var reference: DatabaseReference
    private lateinit var preference: SharedPreference
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var list = arrayListOf<ParkingTicket>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkingDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation()

        reference = Firebase.database.reference
        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        getListBookingParking()


    }

    private fun getListBookingParking() {
        binding.apply {
            rvListParking.inVisible()
            loading.visible()
        }

        db.collection("listBooking")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, error ->

                Log.d("SNAP", snap?.documents.toString())

                binding.apply {
                    rvListParking.visible()
                    loading.gone()
                }

                // if error
                if (error != null) {
                    onError("Listen Failed")
                    return@addSnapshotListener
                }

                // if snap not null
                if (snap != null) {
                    list.clear()
                    for (doc in snap) {
                        list.add(doc.toObject(ParkingTicket::class.java))
                    }
                    setupRecyclerView(list)
                }
            }


    }

    private fun setupRecyclerView(data: ArrayList<ParkingTicket>) {
        val adapter = DashboardAdapter()
        adapter.addData(data)
        binding.apply {
            rvListParking.adapter = adapter
            rvListParking.setHasFixedSize(true)
        }

        if (adapter.itemCount == 0) {
            binding.messageDashboardEmpty.visible()
            onInfo("Booking List Parking is Empty")
        } else {
            binding.messageDashboardEmpty.gone()
        }

    }


    private fun navigation() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()

        binding.btnExitPortal.setOnClickListener {
            reference.child("portal")
                .child("servoExit")
                .setValue("1")

            dialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                delay(5000L)
                reference.child("portal")
                    .child("servoExit")
                    .setValue("0").addOnSuccessListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            dialog.dismiss()
                        }
                    }
            }

        }

        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Dashboard Admin"
            setTitleTextColor(ContextCompat.getColor(this@ParkingDashboardActivity, R.color.white))
        }
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.go_profile_menu -> {
                startActivity(Intent(this@ParkingDashboardActivity,
                    LogoutAdminActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


}