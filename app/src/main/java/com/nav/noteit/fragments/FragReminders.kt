package com.nav.noteit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nav.noteit.R
import com.nav.noteit.databinding.FragRemindersBinding


class FragReminders : FragBase<FragRemindersBinding>() {
    override fun setUpFrag() {

    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragRemindersBinding
        get() = FragRemindersBinding::inflate

    override fun onDetach() {
        super.onDetach()
        selectMenuDrawer()
    }
}