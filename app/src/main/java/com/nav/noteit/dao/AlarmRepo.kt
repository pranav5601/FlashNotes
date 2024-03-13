package com.nav.noteit.dao

import com.nav.noteit.models.AlarmItem

interface AlarmRepo{
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItem: AlarmItem)
}