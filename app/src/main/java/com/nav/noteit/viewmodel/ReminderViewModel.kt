package com.nav.noteit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nav.noteit.repositories.ReminderRepo
import com.nav.noteit.room_models.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(private val reminderRepo: ReminderRepo):ViewModel() {

    private val reminderTime = MutableLiveData<String>()
    private val reminderDate = MutableLiveData<String>()
    private val reminderRepetition = MutableLiveData<String>()
    private val reminder = MutableLiveData<Reminder>()




    fun insertReminder(reminder: Reminder) = viewModelScope.launch (Dispatchers.IO){
        reminderRepo.insertReminder(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        reminderRepo.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder?) = viewModelScope.launch(Dispatchers.IO) {
        reminderRepo.deleteReminder(reminder)
    }




}