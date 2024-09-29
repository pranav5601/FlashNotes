package com.nav.noteit.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nav.noteit.fragments.FragDateTimePicker
import com.nav.noteit.fragments.FragPlacePicker
import com.nav.noteit.room_models.Reminder

class ReminderViewPagerAdapter(fa: FragmentActivity, behaviour: Int,val reminderData: Reminder?): FragmentStateAdapter(fa) {
    override fun getItemCount() = 2


    override fun createFragment(position: Int): Fragment {
        return when(position){
            (0)->{
                FragDateTimePicker().setInstance(reminderData)
            }
            (1)->{
                FragPlacePicker()
            }

            else -> {
                FragDateTimePicker().setInstance(reminderData)
            }
        }
    }


}