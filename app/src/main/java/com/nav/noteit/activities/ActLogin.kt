package com.nav.noteit.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.nav.noteit.R
import com.nav.noteit.databinding.ActLoginBinding
import com.nav.noteit.databinding.ActMainBinding
import com.nav.noteit.fragments.FragLogin
import com.nav.noteit.helper.Utils
import com.nav.noteit.models.UserLogin
import com.nav.noteit.models.UserReq
import com.nav.noteit.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class ActLogin : ActBase() {

    private lateinit var binding: ActLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.act_login)
        setContentView(initBinding())

        loadFragments()

    }
    private fun loadFragments() {

        Utils.replaceFrag(supportFragmentManager,R.id.loginFragContainer,FragLogin(),0,0,0,0)

    }

    private fun initBinding(): View {

        binding = ActLoginBinding.inflate(layoutInflater)
        return binding.root

    }


}