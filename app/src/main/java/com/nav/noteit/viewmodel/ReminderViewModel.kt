package com.nav.noteit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReminderViewModel():ViewModel() {

    private val reminderTime = MutableLiveData<String>()
    private val reminderDate = MutableLiveData<String>()
    private val reminderRepetition = MutableLiveData<String>()


    val selectedTime: LiveData<String> = reminderTime
    val selectedDate: LiveData<String> = reminderDate
    val selectedRepetition: LiveData<String> = reminderRepetition

    fun selectedTime(time: String){
        reminderTime.value = time
    }

    fun selectedDate(date: String){
        reminderDate.value = date
    }

    fun selectedRepetition(repetition: String){
        reminderRepetition.value = repetition
    }

}