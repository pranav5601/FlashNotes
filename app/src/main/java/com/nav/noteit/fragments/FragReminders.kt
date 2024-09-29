package com.nav.noteit.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nav.noteit.adapters.AdapterReminder
import com.nav.noteit.databinding.FragRemindersBinding
import com.nav.noteit.room_models.Reminder
import com.nav.noteit.viewmodel.ReminderViewModel
import org.koin.android.ext.android.inject


class FragReminders : FragBase<FragRemindersBinding>(), AdapterReminder.ReminderClickListener {

    private val reminderViewModel: ReminderViewModel by inject()
    private lateinit var reminderAdapter: AdapterReminder

    override fun setUpFrag() {

        reminderViewModel.getAllReminder(getUserIdFromPrefs()!!)
        initAdapter()
        getReminderData()
    }

    private fun getReminderData() {
            binding.rcvRemindersList.visibility = View.GONE
            binding.tvEmptyReminders.visibility = View.VISIBLE
        reminderViewModel.getReminders.observe(viewLifecycleOwner) { reminders ->

            reminders?.let {
                if (reminders.isNotEmpty()){
                    binding.rcvRemindersList.visibility = View.VISIBLE
                    binding.tvEmptyReminders.visibility = View.GONE

                }
                reminderAdapter.updateList(reminders)
            }
        }
        reminderAdapter.notifyDataSetChanged()
    }

    private fun initAdapter() {
        reminderAdapter = AdapterReminder(baseContext, this)
        binding.rcvRemindersList.setHasFixedSize(true)
        binding.rcvRemindersList.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        binding.rcvRemindersList.adapter = reminderAdapter

    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragRemindersBinding
        get() = FragRemindersBinding::inflate

    override fun onDetach() {
        super.onDetach()
        selectMenuDrawer()
    }

    override fun clickReminder(reminder: Reminder) {

    }
}