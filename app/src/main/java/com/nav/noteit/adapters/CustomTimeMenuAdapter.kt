package com.nav.noteit.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.nav.noteit.R
import com.nav.noteit.models.TimeItem


class CustomTimeMenuAdapter(private val context: Context, private val itemList: List<TimeItem>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): TimeItem {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }




    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.time_menu_cell, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = getItem(position)
        val isActive = item.isActive
        holder.lytTimePicker.isClickable = !isActive



        holder.tvTitle.text = item.mainTitle
        holder.tvDescription.text = item.timeTitle

        return view!!
    }





    private class ViewHolder(view: View) {
        val tvTitle: TextView = view.findViewById(R.id.timeMainTitle)
        val tvDescription: TextView = view.findViewById(R.id.timeSubTitle)
        val lytTimePicker: LinearLayout = view.findViewById(R.id.lytTimePicker)
    }
}