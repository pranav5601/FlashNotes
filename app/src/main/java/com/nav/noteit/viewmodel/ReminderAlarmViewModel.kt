package com.nav.noteit.viewmodel

import androidx.lifecycle.ViewModel
import com.nav.noteit.dao.AlarmRepo
import com.nav.noteit.models.AlarmItem
import com.nav.noteit.repositories.AlarmRepoImpl

class ReminderAlarmViewModel(private val alarmRepo: AlarmRepoImpl): ViewModel() {

    fun scheduleAlarm(alarmItem: AlarmItem){
        alarmRepo.schedule(alarmItem)
    }

    fun cancelAlarm(alarmItem: AlarmItem){
        alarmRepo.cancel(alarmItem)
    }
}