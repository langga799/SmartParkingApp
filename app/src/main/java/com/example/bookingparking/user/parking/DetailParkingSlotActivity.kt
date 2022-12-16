package com.example.bookingparking.user.parking

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityDetailParkingSlotBinding
import com.example.bookingparking.helper.*
import com.example.bookingparking.model.Booking
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailParkingSlotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailParkingSlotBinding
    private lateinit var reference: DatabaseReference
    private lateinit var preference: SharedPreference
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private var timeStamp = ""
    private var userName = ""
    private var userPlate = ""
    private var codeParking = ""
    private var docTicketID = ""

    companion object {
        const val EXTRA_CODE_PARKING = "extra_code_parking"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailParkingSlotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation()


        reference = Firebase.database.reference
        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()


        binding.apply {
            loadingPage.visible()
            messageBooking.inVisible()
            parent.inVisible()
            loading.inVisible()
            btnBookingNow.inVisible()
            btnCancelBooking.inVisible()
        }


        // Request Data Profile
        reqProfile()

        // Check if you have booking
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000L)
            checkIsBooking()
        }

        // Get Data Code Parking
        codeParking = intent.getStringExtra(EXTRA_CODE_PARKING).toString()
        binding.codeParking.text = codeParking


        // Get TimeStamp
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = current.format(formatter)

        val formatTime = DateTimeFormatter.ofPattern("HH:mm:ss")
        val time = current.format(formatTime)
        timeStamp = "$date | $time"


    }

    private fun reqProfile() {
        reference.child("user")
            .child(auth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("fullName").value.toString()
                    val plate = snapshot.child("plateNumber").value.toString()

                    userName = name
                    userPlate = plate
                    Log.d("DATA", name+plate)


                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun checkIsBooking() {
        Log.d("ID_USER", auth.uid.toString())
        reference.child("user").child(auth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when (snapshot.child("stateBooking").value.toString()) {
                        "YES" -> {
                            binding.apply {
                                messageBooking.visible()
                                loadingPage.inVisible()
                                parent.visible()
                                loading.inVisible()
                                btnBookingNow.inVisible()
                                btnCancelBooking.visible()
                            }
                            cancelBooking()
                        }
                        "NO" -> {
                            binding.apply {
                                messageBooking.inVisible()
                                loadingPage.inVisible()
                                parent.visible()
                                loading.inVisible()
                                btnBookingNow.visible()
                                btnCancelBooking.inVisible()
                            }
                            bookingParking(userName, userPlate)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })


    }


    private fun bookingParking(fullName: String, numberPlate: String) {
        val uid = auth.uid.toString()
        val booking = Booking(fullName, numberPlate, codeParking, timeStamp, uid, docTicketID)

        // Booking now
        binding.btnBookingNow.setOnClickListener {
            binding.loading.visible()
            binding.btnBookingNow.inVisible()
            binding.btnCancelBooking.inVisible()
            CoroutineScope(Dispatchers.IO).launch {
                delay(500L)

                // Set ticket list booking
                db.collection("listBooking")
                    .add(booking)
                    .addOnCompleteListener { task ->
                        binding.loading.gone()
                        binding.btnBookingNow.inVisible()
                        binding.btnCancelBooking.visible()

                        docTicketID = task.result.id
                        preference.saveDocumentIdFirestore(task.result.id)
                        Log.d("TASK_ID", task.result.id)


                        val newDocID = hashMapOf(
                            "docTicketID"  to docTicketID
                        )
                        db.collection("listBooking").document(docTicketID)
                            .set(newDocID, SetOptions.merge())


                        val sendBookingToFirestore = Booking(fullName, numberPlate, codeParking, timeStamp, uid, docTicketID)
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(500L)
                            // Set ticket
                            reference.child("user")
                                .child(uid)
                                .child("ticket")
                                .setValue(sendBookingToFirestore)
                        }

                        // Set value YES when cancel button
                        reference.child("user").child(auth.uid.toString())
                            .child("stateBooking")
                            .setValue("YES")

                        // Set value slot parking
                        reference.child("parkingSlot")
                            .child(codeParking)
                            .setValue("1")

                        // Set ongoing parking state
                        reference.child("onGoing")
                            .child(uid)
                            .child("parkingState")
                            .setValue("NO")


                        // set pre-booking slot state
                        reference.child("preBooking")
                            .child(uid)
                            .child("preBookingState")
                            .setValue("YES")

                        reference.child("preBooking")
                            .child(uid)
                            .child("codeParking")
                            .setValue(codeParking)

                        onSuccess("Booking parking successfully")
                    }


            }
        }


    }


    private fun cancelBooking() {
        // Cancel Booking
        binding.btnCancelBooking.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Cancelling Parking")
                .setMessage("Are you sure to cancel?")
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { _, _ ->
                    binding.loading.visible()
                    binding.btnBookingNow.inVisible()
                    binding.btnCancelBooking.inVisible()
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(500L)

                        // Set value slot parking
                        reference.child("parkingSlot")
                            .child(codeParking)
                            .setValue("0")

                        // Remove list ticket
                        db.collection("listBooking")
                            .document(preference.getDocID())
                            .delete()

                        // Set value NO when cancel button
                        reference.child("user")
                            .child(auth.uid.toString())
                            .child("stateBooking")
                            .setValue("NO")

                        // set pre-booking slot state
                        reference.child("preBooking")
                            .child(auth.uid.toString())
                            .child("preBookingState")
                            .setValue("NO")

                        reference.child("preBooking")
                            .child(auth.uid.toString())
                            .child("codeParking")
                            .setValue("-")

                        // Remove ticket
                        reference.child("user")
                            .child(auth.uid.toString())
                            .child("ticket")
                            .removeValue().addOnCompleteListener {
                                binding.loading.gone()
                                binding.btnBookingNow.visible()
                                binding.btnCancelBooking.inVisible()

                                onInfo("Booking parking was cancelled")
                            }
                    }
                }.show()
        }
    }


    private fun navigation() {
        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Detail Slot"
            setTitleTextColor(ContextCompat.getColor(this@DetailParkingSlotActivity,
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