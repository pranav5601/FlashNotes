package com.nav.noteit.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nav.noteit.fragments.FragDateTimePicker
import com.nav.noteit.fragments.FragPlacePicker

class ReminderViewPagerAdapter(fa: FragmentActivity, behaviour: Int): FragmentStateAdapter(fa) {
    override fun getItemCount() = 2


    override fun createFragment(position: Int): Fragment {
        return when(position){
            (0)->{
                FragDateTimePicker()
            }
            (1)->{
                FragPlacePicker()
            }

            else -> {
                FragDateTimePicker()
            }
        }
    }


}