package com.nav.noteit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nav.noteit.R
import com.nav.noteit.databinding.FragSignUpBinding


class FragSignUp : FragBase<FragSignUpBinding>() {


    override fun setUpFrag() {

    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragSignUpBinding
        get() = FragSignUpBinding::inflate

}