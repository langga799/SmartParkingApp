package com.example.bookingparking

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.bookingparking.admin.dashboard.ParkingDashboardActivity
import com.example.bookingparking.databinding.ActivityMainBinding
import com.example.bookingparking.helper.SharedPreference
import com.example.bookingparking.user.auth.LoginUserActivity
import com.example.bookingparking.user.parking.ParkingSlotActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preference: SharedPreference

    companion object {
        private const val DELAY_SCREEN = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        preference = SharedPreference(this)

        Log.d("AccountType", preference.getAccountType())
        Log.d("LoginInfo", preference.getLoginState().toString())

        when(preference.getAccountType()){
            "ADMIN" -> {
                if (preference.getLoginState()){ // is true
                    Handler(mainLooper).postDelayed({
                        startActivity(Intent(this, ParkingDashboardActivity::class.java))
                        finishAffinity()
                    }, DELAY_SCREEN)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, LoginUserActivity::class.java))
                        finishAffinity()
                    }, DELAY_SCREEN)
                }
            }

            "USER" -> {
                if (preference.getLoginState()){ // is true
                    Handler(mainLooper).postDelayed({
                        startActivity(Intent(this, ParkingSlotActivity::class.java))
                        finishAffinity()
                    }, DELAY_SCREEN)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, LoginUserActivity::class.java))
                        finishAffinity()
                    }, DELAY_SCREEN)
                }
            }

            else -> {
                Handler(mainLooper).postDelayed({
                    startActivity(Intent(this, LoginUserActivity::class.java))
                    finishAffinity()
                }, DELAY_SCREEN)
            }

        }


    }
}