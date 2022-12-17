package com.example.bookingparking.user.parking

import android.R.id
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import com.example.bookingparking.R
import com.example.bookingparking.databinding.ActivityParkingSlotBinding
import com.example.bookingparking.helper.SharedPreference
import com.example.bookingparking.user.auth.LogoutUserActivity
import com.example.bookingparking.user.parking.DetailParkingSlotActivity.Companion.EXTRA_CODE_PARKING
import com.google.common.io.Files.append
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ParkingSlotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParkingSlotBinding
    private lateinit var reference: DatabaseReference
    private lateinit var preference: SharedPreference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkingSlotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reference = Firebase.database.reference
        preference = SharedPreference(this)
        auth = FirebaseAuth.getInstance()

        navigation()
      //  parkingSlot()
        getStateSlotParking()
    }

    private fun getStateSlotParking() {
        reference.child("parkingSlot")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.apply {
                        when {
                            // Code parking P01
                            snapshot.child("P01").value.toString() == "1" -> {
                                p01.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.red))

                                reference.child("preBooking")
                                    .child(auth.uid.toString())
                                    .child("codeParking")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value.toString() == "P01") {

                                                val spanBold = SpannableStringBuilder()
                                                    .append("You have booking now in ")
                                                    .bold { append("P01") }

                                                labelInformationBookingParking.text = spanBold

                                                p01.isEnabled = true
                                                p02.isEnabled = false
                                                p03.isEnabled = false
                                                p04.isEnabled = false
                                                p05.isEnabled = false
                                                p06.isEnabled = false

                                                p01.setOnClickListener {
                                                    val intent = Intent(this@ParkingSlotActivity,
                                                        DetailParkingSlotActivity::class.java)
                                                    intent.putExtra(EXTRA_CODE_PARKING, "P01")
                                                    startActivity(intent)
                                                }
                                            } else {
                                               // onError("Sorry.. you have booking")
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })


                            }
                            snapshot.child("P01").value.toString() == "0" -> {
                                p01.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.green))

                                p01.isEnabled = true
                                p01.setOnClickListener {
                                    val intent = Intent(this@ParkingSlotActivity,
                                        DetailParkingSlotActivity::class.java)
                                    intent.putExtra(EXTRA_CODE_PARKING, "P01")
                                    startActivity(intent)
                                }
                            }
                        }

                        when {
                            // Code parking P02
                            snapshot.child("P02").value.toString() == "1" -> {
                                p02.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.red))

                                reference.child("preBooking")
                                    .child(auth.uid.toString())
                                    .child("codeParking")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value.toString() == "P02") {

                                                val spanBold = SpannableStringBuilder()
                                                    .append("You have booking now in ")
                                                    .bold { append("P02") }

                                                labelInformationBookingParking.text = spanBold

                                                p01.isEnabled = false
                                                p02.isEnabled = true
                                                p03.isEnabled = false
                                                p04.isEnabled = false
                                                p05.isEnabled = false
                                                p06.isEnabled = false

                                                p02.setOnClickListener {
                                                    val intent = Intent(this@ParkingSlotActivity,
                                                        DetailParkingSlotActivity::class.java)
                                                    intent.putExtra(EXTRA_CODE_PARKING, "P02")
                                                    startActivity(intent)
                                                }
                                            } else {
                                               // onError("Sorry.. you have booking")
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })
                            }

                            snapshot.child("P02").value.toString() == "0" -> {
                                p02.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.green))

                                p02.isEnabled = true
                                p02.setOnClickListener {
                                    val intent = Intent(this@ParkingSlotActivity,
                                        DetailParkingSlotActivity::class.java)
                                    intent.putExtra(EXTRA_CODE_PARKING, "P02")
                                    startActivity(intent)
                                }
                            }
                        }

                        when {
                            // Code parking P03
                            snapshot.child("P03").value.toString() == "1" -> {
                                p03.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.red))

                                reference.child("preBooking")
                                    .child(auth.uid.toString())
                                    .child("codeParking")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value.toString() == "P03") {

                                                val spanBold = SpannableStringBuilder()
                                                    .append("You have booking now in ")
                                                    .bold { append("P03") }

                                                labelInformationBookingParking.text = spanBold

                                                p01.isEnabled = false
                                                p02.isEnabled = false
                                                p03.isEnabled = true
                                                p04.isEnabled = false
                                                p05.isEnabled = false
                                                p06.isEnabled = false

                                                p03.setOnClickListener {
                                                    val intent = Intent(this@ParkingSlotActivity,
                                                        DetailParkingSlotActivity::class.java)
                                                    intent.putExtra(EXTRA_CODE_PARKING, "P03")
                                                    startActivity(intent)
                                                }
                                            } else {
                                                // onError("Sorry.. you have booking")
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })
                            }
                            snapshot.child("P03").value.toString() == "0" -> {
                                p03.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.green))

                                p03.isEnabled = true
                                p03.setOnClickListener {
                                    val intent = Intent(this@ParkingSlotActivity,
                                        DetailParkingSlotActivity::class.java)
                                    intent.putExtra(EXTRA_CODE_PARKING, "P03")
                                    startActivity(intent)
                                }
                            }
                        }

                        when {
                            // Code parking P04
                            snapshot.child("P04").value.toString() == "1" -> {
                                p04.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.red))

                                reference.child("preBooking")
                                    .child(auth.uid.toString())
                                    .child("codeParking")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value.toString() == "P04") {

                                                val spanBold = SpannableStringBuilder()
                                                    .append("You have booking now in ")
                                                    .bold { append("P04") }

                                                labelInformationBookingParking.text = spanBold

                                                p01.isEnabled = false
                                                p02.isEnabled = false
                                                p03.isEnabled = false
                                                p04.isEnabled = true
                                                p05.isEnabled = false
                                                p06.isEnabled = false

                                                p04.setOnClickListener {
                                                    val intent = Intent(this@ParkingSlotActivity,
                                                        DetailParkingSlotActivity::class.java)
                                                    intent.putExtra(EXTRA_CODE_PARKING, "P04")
                                                    startActivity(intent)
                                                }
                                            } else {
                                                // onError("Sorry.. you have booking")
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })
                            }
                            snapshot.child("P04").value.toString() == "0" -> {
                                p04.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.green))

                                p04.isEnabled = true
                                p04.setOnClickListener {
                                    val intent = Intent(this@ParkingSlotActivity,
                                        DetailParkingSlotActivity::class.java)
                                    intent.putExtra(EXTRA_CODE_PARKING, "P04")
                                    startActivity(intent)
                                }
                            }
                        }

                        when {
                            // Code parking P05
                            snapshot.child("P05").value.toString() == "1" -> {
                                p05.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.red))

                                reference.child("preBooking")
                                    .child(auth.uid.toString())
                                    .child("codeParking")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value.toString() == "P05") {

                                                val spanBold = SpannableStringBuilder()
                                                    .append("You have booking now in ")
                                                    .bold { append("P05") }

                                                labelInformationBookingParking.text = spanBold

                                                p01.isEnabled = false
                                                p02.isEnabled = false
                                                p03.isEnabled = false
                                                p04.isEnabled = false
                                                p05.isEnabled = true
                                                p06.isEnabled = false

                                                p05.setOnClickListener {
                                                    val intent = Intent(this@ParkingSlotActivity,
                                                        DetailParkingSlotActivity::class.java)
                                                    intent.putExtra(EXTRA_CODE_PARKING, "P05")
                                                    startActivity(intent)
                                                }
                                            } else {
                                                // onError("Sorry.. you have booking")
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })
                            }
                            snapshot.child("P05").value.toString() == "0" -> {
                                p05.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.green))

                                p05.isEnabled = true
                                p05.setOnClickListener {
                                    val intent = Intent(this@ParkingSlotActivity,
                                        DetailParkingSlotActivity::class.java)
                                    intent.putExtra(EXTRA_CODE_PARKING, "P05")
                                    startActivity(intent)
                                }
                            }
                        }

                        when {
                            // Code parking P06
                            snapshot.child("P06").value.toString() == "1" -> {
                                p06.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.red))

                                reference.child("preBooking")
                                    .child(auth.uid.toString())
                                    .child("codeParking")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value.toString() == "P06") {

                                                val spanBold = SpannableStringBuilder()
                                                    .append("You have booking now in ")
                                                    .bold { append("P06") }

                                                labelInformationBookingParking.text = spanBold

                                                p01.isEnabled = false
                                                p02.isEnabled = false
                                                p03.isEnabled = false
                                                p04.isEnabled = false
                                                p05.isEnabled = false
                                                p06.isEnabled = true

                                                p06.setOnClickListener {
                                                    val intent = Intent(this@ParkingSlotActivity,
                                                        DetailParkingSlotActivity::class.java)
                                                    intent.putExtra(EXTRA_CODE_PARKING, "P06")
                                                    startActivity(intent)
                                                }
                                            } else {
                                                // onError("Sorry.. you have booking")
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {}

                                    })
                            }
                            snapshot.child("P06").value.toString() == "0" -> {
                                p06.setCardBackgroundColor(ContextCompat.getColor(this@ParkingSlotActivity,
                                    R.color.green))

                                p06.isEnabled = true
                                p06.setOnClickListener {
                                    val intent = Intent(this@ParkingSlotActivity,
                                        DetailParkingSlotActivity::class.java)
                                    intent.putExtra(EXTRA_CODE_PARKING, "P06")
                                    startActivity(intent)
                                }
                            }

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }

    private fun parkingSlot() {
        binding.apply {
            p01.setOnClickListener {

//                reference.child("preBooking")
//                    .child(auth.uid.toString())
//                    .child("codeParking")
//                    .addValueEventListener(object : ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if ()
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//
//                    })

                val intent = Intent(this@ParkingSlotActivity, DetailParkingSlotActivity::class.java)
                intent.putExtra(EXTRA_CODE_PARKING, "P01")
                startActivity(intent)
            }

            p02.setOnClickListener {
                val intent = Intent(this@ParkingSlotActivity, DetailParkingSlotActivity::class.java)
                intent.putExtra(EXTRA_CODE_PARKING, "P02")
                startActivity(intent)
            }

            p03.setOnClickListener {
                val intent = Intent(this@ParkingSlotActivity, DetailParkingSlotActivity::class.java)
                intent.putExtra(EXTRA_CODE_PARKING, "P03")
                startActivity(intent)
            }

            p04.setOnClickListener {
                val intent = Intent(this@ParkingSlotActivity, DetailParkingSlotActivity::class.java)
                intent.putExtra(EXTRA_CODE_PARKING, "P04")
                startActivity(intent)
            }

            p05.setOnClickListener {
                val intent = Intent(this@ParkingSlotActivity, DetailParkingSlotActivity::class.java)
                intent.putExtra(EXTRA_CODE_PARKING, "P05")
                startActivity(intent)
            }

            p06.setOnClickListener {
                val intent = Intent(this@ParkingSlotActivity, DetailParkingSlotActivity::class.java)
                intent.putExtra(EXTRA_CODE_PARKING, "P06")
                startActivity(intent)
            }



        }
    }

    private fun navigation() {
        // Goto Parking Ticket
        binding.btnToMyTicket.setOnClickListener {
            val intent = Intent(this@ParkingSlotActivity, TicketActivity::class.java)
            startActivity(intent)
        }

        val toolbar = binding.toolbar
        toolbar.apply {
            title = "Dashboard User"
            setTitleTextColor(ContextCompat.getColor(this@ParkingSlotActivity, R.color.white))
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
                startActivity(Intent(this@ParkingSlotActivity,
                    LogoutUserActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}