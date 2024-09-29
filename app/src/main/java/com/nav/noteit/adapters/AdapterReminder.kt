package com.nav.noteit.adapters

import android.app.AlarmManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nav.noteit.R
import com.nav.noteit.adapters.AdapterNotes.MyViewHolder
import com.nav.noteit.room_models.Reminder

class AdapterReminder(

    private val context: Context,
    private val reminderClick: ReminderClickListener
): Adapter<AdapterReminder.MyViewHolder>() {


     private var reminderList = ArrayList<Reminder>()


    class MyViewHolder(itemView: View): ViewHolder(itemView){


        private val tvTime = itemView.findViewById<TextView>(R.id.txtReminderTime)
        private val tvTitle = itemView.findViewById<TextView>(R.id.txtReminderTitle)
        private val tvRepetition = itemView.findViewById<TextView>(R.id.txtReminderRepetition)
        private val lytCellReminder = itemView.findViewById<ConstraintLayout>(R.id.lyt_cell_reminder)

        fun binding(reminder: Reminder, context: Context, reminderClick: ReminderClickListener) {
            tvTime.text = reminder.reminderTime
            tvTitle.text = reminder.title

            when(reminder.reminderRepetition){
                (0L)->{

                    tvRepetition.text = "This will never repeat."
                }
                (AlarmManager.INTERVAL_DAY) -> {
                    tvRepetition.text = context.getString(R.string.repeats_daily)
                }

                (AlarmManager.INTERVAL_DAY*7) -> {
                    tvRepetition.text = context.getString(R.string.repeats_weekly)
                }
                (AlarmManager.INTERVAL_DAY*30) -> {
                    tvRepetition.text = context.getString(R.string.repeats_monthly)
                }
                (AlarmManager.INTERVAL_DAY*7) -> {
                    tvRepetition.text = context.getString(R.string.repeats_yearly)
                }

                else ->{
                }

            }

            lytCellReminder.setOnClickListener {
                reminderClick.clickReminder(reminder)
            }

        }


    }

    fun updateList(reminder: List<Reminder>){
        reminderList.clear()
        reminderList.addAll(reminder)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cell_reminder_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = reminderList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding(reminderList[position],context, reminderClick)
    }
interface ReminderClickListener {
    fun clickReminder(reminder:Reminder)


}
}

