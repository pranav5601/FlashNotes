package com.nav.noteit.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nav.noteit.R
import com.nav.noteit.remote.local_data.UserPreferences
import com.nav.noteit.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class ActSplash : ActBase() {


    private lateinit var userId: String
    private val userViewModel by inject<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)

           userViewModel.getUserDataFromPrefs()


        lifecycleScope.launch {


            userViewModel.userData.observe(this@ActSplash){userData ->


                userId = userData.userId!!

            }

            delay(1000)
            checkForExistingUser()
        }


        runBlocking {


//                startAnimation()

        }

    }

//    private fun startAnimation() {
//        val logoAnim = findViewById<LottieAnimationView>(R.id.logo_animation)
//        logoAnim.setAnimation("noteit_logo_animation.json")
//
//        logoAnim.playAnimation()
//    }

    private fun checkForExistingUser() {
        Log.e("userId", userId)
        if (userId.isNotEmpty()) {

            val mainIntent = Intent(this, ActMain::class.java)
            startActivity(mainIntent)
            finish()

        } else {
            val loginIntent = Intent(this, ActLogin::class.java)
            startActivity(loginIntent)
            finish()

        }
    }
}