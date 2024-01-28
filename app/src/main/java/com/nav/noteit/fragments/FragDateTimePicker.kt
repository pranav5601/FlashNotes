package com.nav.noteit.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nav.noteit.databinding.FragDateTimePickerBinding
import com.nav.noteit.viewmodel.ReminderViewModel
import org.koin.android.ext.android.inject


class FragDateTimePicker : FragBase<FragDateTimePickerBinding>() {



    private lateinit var selectedDate:String
    private lateinit var selectedTime:String
    private val reminderViewModel by inject<ReminderViewModel> ()
    private lateinit var filter :IntentFilter
    private lateinit var reminderBroadcastReceiver: BroadcastReceiver

    override fun setUpFrag() {

        setUpTimeSpinner()
        setDateSpinner()
        setUpRepetition()

        whenReceivedBroadcast()

    }

    private fun whenReceivedBroadcast() {
        filter = IntentFilter().apply { addAction("click") }

          reminderBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.e("Save", onSaveClickListener().toString())
                Toast.makeText(baseContext,onSaveClickListener().toString(),Toast.LENGTH_SHORT).show()
                LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(reminderBroadcastReceiver)

            }
        }
        LocalBroadcastManager.getInstance(baseContext).registerReceiver(reminderBroadcastReceiver,filter)
    }


    override fun onPause() {
        super.onPause()
        Log.e("FragDateTimePicker","onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("FragDateTimePicker","onDestroy")
        LocalBroadcastManager.getInstance(baseContext).unregisterReceiver(reminderBroadcastReceiver)
    }
    private fun initClick() {

    }

    private fun setUpRepetition() {
        val repetitionList: ArrayList<String> = ArrayList<String>()
        repetitionList.add("Never Repeat")
        repetitionList.add("Every Day")
        repetitionList.add("Every Week")
        repetitionList.add("Every Month")
        repetitionList.add("Every Year")


        val dateDataAdapter: ArrayAdapter<String> = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item,repetitionList)

        dateDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spnRepetition.adapter = dateDataAdapter
    }

    private fun setDateSpinner() {

        val dateList: ArrayList<String> = ArrayList<String>()
        dateList.add("Today")
        dateList.add("Tomorrow")
        dateList.add("Next Week")
        dateList.add("Pick a Date...")


        val dateDataAdapter: ArrayAdapter<String> = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item,dateList)

        dateDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spnDate.adapter = dateDataAdapter

//        selectedDate = binding.spnDate.selectedItem.toString()
    }



    private fun setUpTimeSpinner() {
        val timeList: ArrayList<String> = ArrayList<String>()
        timeList.add("Morning 8:00 AM")
        timeList.add("Afternoon 1:00 PM")
        timeList.add("Evening 6:00 PM")
        timeList.add("Night 8:00 PM")
        timeList.add("Pick a Time...")


        val dateDataAdapter: ArrayAdapter<String> = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item,timeList)

        dateDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spnTime.adapter = dateDataAdapter
//        selectedDate = binding.spnDate.selectedItem.toString()

    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragDateTimePickerBinding
        get() = FragDateTimePickerBinding::inflate

    fun onSaveClickListener(): List<String> {
        val time = binding.spnTime.selectedItem.toString()
        val date = binding.spnDate.selectedItem.toString()
        val repetition = binding.spnRepetition.selectedItem.toString()
        reminderViewModel.selectedTime(time)
        reminderViewModel.selectedDate(date)
        reminderViewModel.selectedRepetition(repetition)
        val listOfVal: List<String> = listOf(time,date,repetition)
        return listOfVal
    }


}