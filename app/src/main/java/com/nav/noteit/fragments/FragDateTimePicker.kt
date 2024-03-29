package com.nav.noteit.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.nav.noteit.R
import com.nav.noteit.adapters.CustomDateMenuAdapter
import com.nav.noteit.adapters.CustomRepetitionAdapter
import com.nav.noteit.adapters.CustomTimeMenuAdapter
import com.nav.noteit.databinding.FragDateTimePickerBinding
import com.nav.noteit.helper.Constants
import com.nav.noteit.models.TimeItem
import com.nav.noteit.room_models.Reminder
import com.nav.noteit.viewmodel.ReminderViewModel
import org.koin.android.ext.android.inject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class FragDateTimePicker : FragBase<FragDateTimePickerBinding>(),
    AdapterView.OnItemSelectedListener {


    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private lateinit var filter: IntentFilter
    private lateinit var reminderBroadcastReceiver: BroadcastReceiver
    private var dateList: ArrayList<String> = ArrayList()
    private lateinit var currentTime: String
    private lateinit var currentDate: String
    private lateinit var selectedRepetition: String
    private var noteId: Int = 0
    private var reminderHour: Int = 0
    private var reminderMin: Int = 0
    private var reminderDate: Int = 0
    private lateinit var reminderCalender: Calendar
    private var reminderAlarmManager: AlarmManager? = null
    private lateinit var alarmPendingIntent: PendingIntent
    private var reminderId: Int? = null
    private lateinit var reminder: Reminder
    private var startPoint = 0

    private var reminderRepetition: Long = 0

    override fun setUpFrag() {

        setCurrentTimeAndDate()
        setUpTimeSpinner()
        setDateSpinner()
        setUpRepetition()
        initClick()
        whenReceivedBroadcast()


    }



    private fun setCurrentTimeAndDate() {

        reminderCalender = Calendar.getInstance()
        val time = Calendar.getInstance().time
        val currentTimeFormat = SimpleDateFormat("KK:mm aaa", Locale.getDefault())
        currentTime = currentTimeFormat.format(time)
        reminderHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val currentDateFormat = SimpleDateFormat("LLLL dd", Locale.getDefault())
        currentDate = currentDateFormat.format(time)


    }

    private fun whenReceivedBroadcast() {
        filter = IntentFilter().apply { addAction("click") }

        reminderBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                noteId = intent.getIntExtra("clicked",0)
                reminderId = intent.getIntExtra("reminder_id", 0)
                Log.e("Save", onSaveClickListener().toString())
                Log.e("note id", noteId.toString())
                Toast.makeText(baseContext, onSaveClickListener().toString(), Toast.LENGTH_SHORT)
                    .show()
                LocalBroadcastManager.getInstance(baseContext)
                    .unregisterReceiver(reminderBroadcastReceiver)

            }
        }
        LocalBroadcastManager.getInstance(baseContext)
            .registerReceiver(reminderBroadcastReceiver, filter)
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


        val repeatAdapter = CustomRepetitionAdapter(baseContext, repetitionList)


        binding.spnRepetition.onItemSelectedListener = this
        binding.spnRepetition.adapter = repeatAdapter
    }

    private fun setDateSpinner() {

//        val dateList: ArrayList<String> = ArrayList<String>()
        dateList.add("Today")
        dateList.add("Tomorrow")
        dateList.add("Next Week")
        dateList.add(Constants.pickDate)

        val customDateMenuAdapter = CustomDateMenuAdapter(baseContext, dateList)
        binding.spnDate.adapter = customDateMenuAdapter

        if (reminderHour in 21 .. 24){
            binding.spnDate.setSelection(1)
        }else{
            binding.spnDate.setSelection(0)
        }

        binding.spnDate.onItemSelectedListener = this

//        selectedDate = binding.spnDate.selectedItem.toString()
    }


    private fun setUpTimeSpinner() {
        val itemList: MutableList<TimeItem> = ArrayList()
        itemList.add(TimeItem("Morning", "8:00 AM", setActiveTime()[0]!!))
        itemList.add(TimeItem("Afternoon", "1:00 PM", setActiveTime()[1]!!))
        itemList.add(TimeItem("Evening", "6:00 PM", setActiveTime()[2]!!))
        itemList.add(TimeItem("Night", "9:00 PM", setActiveTime()[3]!!))
        itemList.add(TimeItem(resources.getString(R.string.pick_your_time), selectedTime, true))

        val customAdapter = CustomTimeMenuAdapter(baseContext, itemList)

        binding.spnTime.adapter = customAdapter


        for (i in setActiveTime()) {
            if (i.value) {
                startPoint = i.key
                break
            }
        }

        binding.spnTime.setSelection(startPoint)


        binding.spnTime.onItemSelectedListener = this
//        selectedDate = binding.spnDate.selectedItem.toString()

    }

    private fun setActiveTime(): HashMap<Int, Boolean> {

        val timeMap: HashMap<Int, Boolean> = HashMap()


        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..8, in 21..24 -> {

                timeMap[0] = true
                timeMap[1] = true
                timeMap[2] = true
                timeMap[3] = true
            }

            in 9 .. 13 -> {
                timeMap[0] = false
                timeMap[1] = true
                timeMap[2] = true
                timeMap[3] = true
            }

            in 14 .. 17 -> {
                timeMap[0] = false
                timeMap[1] = false
                timeMap[2] = true
                timeMap[3] = true
            }

            in 18..20 -> {
                timeMap[0] = false
                timeMap[1] = false
                timeMap[2] = false
                timeMap[3] = true
            }
        }

        return timeMap
    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragDateTimePickerBinding
        get() = FragDateTimePickerBinding::inflate

    fun onSaveClickListener(): List<String> {
        val time: TimeItem? = binding.spnTime.selectedItem as? TimeItem

        if (time?.timeTitle?.isEmpty()!!) {
            time.timeTitle = selectedTime
        }

        val date = binding.spnDate.selectedItem.toString()
        val repetition = binding.spnRepetition.selectedItem.toString()
        Log.e("reminderCalender", reminderCalender.timeInMillis.toString() + repetition)

        val reminderDate = Reminder(
            null,
            noteId,
            time.timeTitle,
            selectedDate,
            reminderRepetition,
            reminderCalender.timeInMillis,
            0,
            0
        )
        val sendData = Intent("send_data").apply {
            putExtra("reminder_data", Gson().toJson(reminderDate))
        }
        LocalBroadcastManager.getInstance(baseContext).sendBroadcast(sendData)

        return listOf(time.timeTitle, date, repetition)
    }




    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, p3: Long) {


        when (parent?.adapter) {
            (binding.spnTime.adapter) -> {
                val mainTitle = view?.findViewById<TextView>(R.id.timeMainTitle)
                val subTitle = view?.findViewById<TextView>(R.id.timeSubTitle)


                when (pos) {
                    (0) -> {

                        reminderCalender.set(Calendar.HOUR_OF_DAY, 8)
                        reminderCalender.set(Calendar.MINUTE, 0)
                        reminderCalender.set(Calendar.SECOND, 0)
                        mainTitle?.text = getString(R.string._08_00_am)
                        subTitle?.visibility = View.GONE


                    }

                    (1) -> {
                        reminderCalender.set(Calendar.HOUR_OF_DAY, 13)
                        reminderCalender.set(Calendar.MINUTE, 0)
                        reminderCalender.set(Calendar.SECOND, 0)

                        mainTitle?.text = getString(R.string._01_00_pm)
                        subTitle?.visibility = View.GONE


                    }

                    (2) -> {
                        reminderCalender.set(Calendar.HOUR_OF_DAY, 18)
                        reminderCalender.set(Calendar.MINUTE, 0)
                        reminderCalender.set(Calendar.SECOND, 0)

                        mainTitle?.text = getString(R.string._06_00_pm)
                        subTitle?.visibility = View.GONE

                    }

                    (3) -> {
                        reminderCalender.set(Calendar.HOUR_OF_DAY, 21)
                        reminderCalender.set(Calendar.MINUTE, 0)
                        reminderCalender.set(Calendar.SECOND, 0)

                        mainTitle?.text = getString(R.string._09_00_pm)
                        subTitle?.visibility = View.GONE

                    }

                    (4) -> {
                        val picker = MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_12H)
                            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                            .setTitleText("Pick a time...")
                            .build()

                        openClock(picker, mainTitle, subTitle, parent)

                    }


                }
            }

            (binding.spnDate.adapter) -> {
                val mainDateTitle = view?.findViewById<TextView>(R.id.mainDateTitle)
                val currentDateFormat = SimpleDateFormat("LLLL dd", Locale.getDefault())
                val c = Calendar.getInstance()

                try {
                    c.time = currentDateFormat.parse(currentDate) as Date
                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    c.set(Calendar.YEAR, year)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                val dateValidator: DateValidator =
                    DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds())
                val calenderConstraint =
                    CalendarConstraints.Builder().setValidator(dateValidator).build()

                val currentDateInUtc = Calendar.getInstance().apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.timeInMillis
                val localTimeZone = TimeZone.getDefault()
                val localOffset = localTimeZone.getOffset(currentDateInUtc)


                when (pos) {
                    (0) -> {
                        reminderCalender.set(Calendar.DATE, c.get(Calendar.DATE))
                        reminderCalender.set(Calendar.MONTH, c.get(Calendar.MONTH))
                        reminderCalender.set(Calendar.YEAR, c.get(Calendar.YEAR))
                        mainDateTitle?.text = currentDate
                        selectedDate = currentDate
                    }

                    (1) -> {

                        c.add(Calendar.DAY_OF_MONTH, 1)
                        reminderCalender.set(Calendar.DATE, c.get(Calendar.DATE))
                        reminderCalender.set(Calendar.MONTH, c.get(Calendar.MONTH))
                        reminderCalender.set(Calendar.YEAR, c.get(Calendar.YEAR))
                        mainDateTitle?.text = currentDateFormat.format(c.time)
                        selectedDate = currentDateFormat.format(c.time)
                    }

                    (2) -> {
                        c.add(Calendar.DAY_OF_MONTH, 7)
                        reminderCalender.set(Calendar.DATE, c.get(Calendar.DATE))
                        reminderCalender.set(Calendar.MONTH, c.get(Calendar.MONTH))
                        reminderCalender.set(Calendar.YEAR, c.get(Calendar.YEAR))
                        mainDateTitle?.text = currentDateFormat.format(c.time)
                        selectedDate = currentDateFormat.format(c.time)

                    }

                    (3) -> {

                        val adjustedDateInUtc = currentDateInUtc + localOffset

                        val reminderDatePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Pick a date")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .setPositiveButtonText("Submit")
                            .setCalendarConstraints(calenderConstraint)
                            .build()

                        Log.e("time", MaterialDatePicker.todayInUtcMilliseconds().toString())



                        openCalender(reminderDatePicker, mainDateTitle, parent)
                    }
                }


            }

            (binding.spnRepetition.adapter) -> {

                val mainRepetitionTitle = view?.findViewById<TextView>(R.id.mainRepeatTitle)

                when (pos) {
                    (0) -> {


                        reminderRepetition = 0
                        mainRepetitionTitle?.text = getString(R.string.does_not_repeat)

                    }

                    (1) -> {
                        reminderRepetition = AlarmManager.INTERVAL_DAY
                        mainRepetitionTitle?.text = getString(R.string.repeats_daily)

                    }

                    (2) -> {
                        reminderRepetition = AlarmManager.INTERVAL_DAY * 7
                        mainRepetitionTitle?.text = getString(R.string.repeats_weekly)

                    }

                    (3) -> {

                        val totalDaysInMonth = reminderCalender.get(Calendar.DAY_OF_MONTH)
                        reminderRepetition = totalDaysInMonth * 24 * 60 * 60 * 1000L
                        mainRepetitionTitle?.text = getString(R.string.repeats_monthly)

                    }

                    (4) -> {
                        val totalDaysInYear = reminderCalender.get(Calendar.DAY_OF_YEAR)
                        reminderRepetition = totalDaysInYear * 24 * 60 * 60 * 1000L
                        mainRepetitionTitle?.text = getString(R.string.repeats_yearly)

                    }

                }


            }
        }


    }

    private fun openCalender(
        reminderDatePicker: MaterialDatePicker<Long>,
        mainDateTitle: TextView?,
        parent: AdapterView<*>?,
    ) {


        reminderDatePicker.addOnPositiveButtonClickListener { timeMillis ->

            Log.e("selected Date", timeMillis.toString() + " " + Locale.getDefault())
            var date = 0L

            val isDayTimeSavingOn =
                TimeZone.getDefault().inDaylightTime(Calendar.getInstance().time)
            val tempCal = Calendar.getInstance()
            date = if (isDayTimeSavingOn) {
                timeMillis + 86400000
            } else {
                timeMillis
            }

            Log.e("isDayTimeSavingOn", isDayTimeSavingOn.toString())
            val simpleDateFormat = SimpleDateFormat("LLLL dd", Locale.getDefault())
            val formattedDate = simpleDateFormat.format(date)
            tempCal.timeInMillis = date

            reminderCalender.set(Calendar.DATE, tempCal.get(Calendar.DATE))
            reminderCalender.set(Calendar.MONTH, tempCal.get(Calendar.MONTH))
            reminderCalender.set(Calendar.YEAR, tempCal.get(Calendar.YEAR))

            mainDateTitle?.text = formattedDate
//            reminderCalender.timeInMillis = timeMillis
            selectedDate = formattedDate

            Toast.makeText(baseContext, formattedDate, Toast.LENGTH_SHORT).show()
        }
        reminderDatePicker.addOnNegativeButtonClickListener {
            reminderDatePicker.addOnNegativeButtonClickListener {
                parent?.setSelection(0)
            }
            reminderDatePicker.addOnDismissListener {
                parent?.setSelection(0)
            }
        }

        reminderDatePicker.show(baseContext.supportFragmentManager, "Reminder DatePicker")

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

        when (parent?.adapter) {
            (binding.spnTime.adapter) -> {
                parent?.setSelection(0)
            }

            (binding.spnDate.adapter) -> {
                parent?.setSelection(0)
            }

            (binding.spnRepetition.adapter) -> {
                parent?.setSelection(0)
            }
        }
    }


    private fun openClock(
        picker: MaterialTimePicker,
        mainTitle: TextView?,
        subTitle: TextView?,
        parent: AdapterView<*>?
    ) {
        picker.show(baseContext.supportFragmentManager, "Time Picker")



        picker.addOnPositiveButtonClickListener {

            var hour = ""
            var amPm = ""

            when (picker.hour) {
                in 13..21 -> {

                    hour = "0" + (picker.hour - 12).toString()
                    amPm = "PM"
                }

                in 22..24 -> {
                    hour = (picker.hour - 12).toString()
                    amPm = "PM"
                }

                in 1..9 -> {
                    hour = "0" + (picker.hour).toString()
                    amPm = "AM"
                }

                in 10..12 -> {
                    hour = (picker.hour).toString()
                    amPm = "AM"
                }
            }

            val minute = if (picker.minute.toString().length < 2) {
                "0${picker.minute}"
            } else {
                picker.minute.toString()
            }



            reminderCalender.set(Calendar.HOUR_OF_DAY, picker.hour)
            reminderCalender.set(Calendar.MINUTE, picker.minute)
            reminderCalender.set(Calendar.SECOND, 0)

            selectedTime = getString(R.string.formatted_time, hour, minute, amPm)
            mainTitle?.text = selectedTime
            subTitle?.visibility = View.GONE
        }

        picker.addOnNegativeButtonClickListener {
            parent?.setSelection(startPoint)
        }
//        picker.addOnDismissListener {
//            parent?.setSelection(0)
//        }


    }

    /*private fun showListPopupWindow(anchor: View) {
        val popupWindow = ListPopupWindow(baseContext)
        val itemList: MutableList<TimeItem> = ArrayList()
//        itemList.add(TimeItem("Morning", "8:00 AM"))
//        itemList.add(TimeItem("Afternoon", "1:00 PM"))
//        itemList.add(TimeItem("Evening", "6:00 PM"))
//        itemList.add(TimeItem("Night", "9:00 PM"))
//        itemList.add(TimeItem(resources.getString(R.string.pick_your_time), ""))

        val adapter = CustomTimeMenuAdapter(baseContext, itemList)
        popupWindow.anchorView = anchor



        popupWindow.setAdapter(adapter)
        popupWindow.setOnItemClickListener { parent, view, i, l ->

            when (i) {
                (0) -> {
                    Toast.makeText(baseContext, "Morning Clicked", Toast.LENGTH_SHORT).show()
                }

                (1) -> {
                    Toast.makeText(baseContext, "Afternoon Clicked", Toast.LENGTH_SHORT).show()
                }

                (2) -> {
                    Toast.makeText(baseContext, "Evening Clicked", Toast.LENGTH_SHORT).show()
                }

                (3) -> {
                    Toast.makeText(baseContext, "Night Clicked", Toast.LENGTH_SHORT).show()
                }

                (4) -> {
                    Toast.makeText(
                        baseContext,
                        resources.getString(R.string.pick_your_time),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            popupWindow.dismiss()
        }
        popupWindow.show()
    }*/

}