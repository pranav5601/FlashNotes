package com.nav.noteit.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

abstract class ActBase : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)





    }

    override fun onDestroy() {
        super.onDestroy()

    }


}