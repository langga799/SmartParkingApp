package com.example.bookingparking.admin.dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityDetailParkingConfirmationBinding
import com.example.bookingparking.helper.inVisible
import com.example.bookingparking.helper.onInfo
import com.example.bookingparking.helper.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailParkingConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailParkingConfirmationBinding
    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        const val EXTRA_PLATE_NUMBER = "extra_plate_number"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_CODE_PARKING = "extra_code_parking"
        const val EXTRA_UID = "extra_uid"
        const val EXTRA_DOC_ID = "extra_doc_id"
    }

    private var userID = ""
    private var dataCodeParking = ""
    private var docID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailParkingConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation()


        // initializing variable
        reference = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        // Check state condition ongoing parking
        reference.child("onGoing")
            .child(intent.getStringExtra(EXTRA_UID).toString())
            .child("parkingState")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when(snapshot.value.toString()){
                        "YES" -> {
                            binding.btnAccept.visible()
                            binding.btnAccept.isEnabled = false
                            binding.btnAccept.setCardBackgroundColor(ContextCompat.getColor(
                                this@DetailParkingConfirmationActivity,
                                R.color.disable))
                            binding.btnClose.setCardBackgroundColor(ContextCompat.getColor(
                                this@DetailParkingConfirmationActivity,
                                R.color.red))
                            binding.btnClose.isEnabled = true

                            setDataParking()
                        }
                        "NO" -> {
                            setDataParking()
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })



        binding.btnClose.setCardBackgroundColor(ContextCompat.getColor(this, R.color.disable))
        binding.btnClose.isEnabled = false

        confirmation()
    }


    private fun confirmation() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress)
        val dialog = builder.create()

        binding.btnAccept.setOnClickListener {
            onInfo("Open Portal")
            reference.child("portal")
                .child("servoEntrance")
                .setValue("1")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.btnAccept.inVisible()
                            dialog.show()
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(5000L)
                            reference.child("portal")
                                .child("servoEntrance")
                                .setValue("0")
                                .addOnSuccessListener {
                                    onInfo("Portal Closed Again")
                                    dialog.dismiss()
                                }

                            binding.btnAccept.visible()
                            binding.btnAccept.isEnabled = false
                            binding.btnAccept.setCardBackgroundColor(ContextCompat.getColor(
                                this@DetailParkingConfirmationActivity,
                                R.color.disable))
                            binding.btnClose.setCardBackgroundColor(ContextCompat.getColor(
                                this@DetailParkingConfirmationActivity,
                                R.color.red))
                            binding.btnClose.isEnabled = true


                            // Set state ongoing parking from admin after car entrance
                            reference.child("onGoing")
                                .child(userID)
                                .child("parkingState")
                                .setValue("YES")

                        }


                    }
                }


        }



        binding.btnClose.setOnClickListener {
            onInfo("Open Portal")
            reference.child("portal")
                .child("servoExit")
                .setValue("1")
                .addOnSuccessListener {
                    binding.btnClose.inVisible()
                    dialog.show()
                }

            CoroutineScope(Dispatchers.Main).launch {
                delay(5000L)

                reference.child("portal")
                    .child("servoExit")
                    .setValue("0")
                    .addOnSuccessListener {
                        dialog.dismiss()
                        onInfo("Portal Closed Again")


                        reference.child("user")
                            .child(userID)
                            .child("ticket")
                            .removeValue()

                        reference.child("user")
                            .child(userID)
                            .child("stateBooking")
                            .setValue("NO")

                        reference.child("parkingSlot")
                            .child(dataCodeParking)
                            .setValue("0")

                        // set pre-booking slot state
                        reference.child("preBooking")
                            .child(auth.uid.toString())
                            .child("preBookingState")
                            .setValue("NO")

                        reference.child("preBooking")
                            .child(auth.uid.toString())
                            .child("codeParking")
                            .setValue("-")

                        db.collection("listBooking")
                            .document(docID)
                            .delete()
                            .addOnSuccessListener {
                                onBackPressedDispatcher.onBackPressed()
                            }

                        // Set ongoing parking state
                        reference.child("onGoing")
                            .child(userID)
                            .child("parkingState")
                            .setValue("NO")
                        Log.d("DOC-ID", docID)
                    }


                binding.btnAccept.isEnabled = true
                binding.btnAccept.setCardBackgroundColor(ContextCompat.getColor(
                    this@DetailParkingConfirmationActivity,
                    R.color.green))
                binding.btnClose.visible()
                binding.btnClose.setCardBackgroundColor(ContextCompat.getColor(
                    this@DetailParkingConfirmationActivity,
                    R.color.disable))
                binding.btnClose.isEnabled = false
            }

        }
    }


    private fun setDataParking() {
        val dataName = intent.getStringExtra(EXTRA_NAME).toString()
        val dataPlateNumber = intent.getStringExtra(EXTRA_PLATE_NUMBER).toString()
        dataCodeParking = intent.getStringExtra(EXTRA_CODE_PARKING).toString()
        userID = intent.getStringExtra(EXTRA_UID).toString()
        docID = intent.getStringExtra(EXTRA_DOC_ID).toString()

        binding.apply {
            tvName.text = dataName
            tvCodeParking.text = dataCodeParking
            tvPlateNumber.text = dataPlateNumber
        }
    }

    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Parking Confirmation"
            setTitleTextColor(ContextCompat.getColor(this@DetailParkingConfirmationActivity,
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