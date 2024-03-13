package com.nav.noteit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.nav.noteit.R

class CustomDateMenuAdapter(private val context: Context, private val dateList: List<String>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return dateList.size
    }

    override fun getItem(position: Int): String {
        return dateList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.date_menu_cell, null)
            holder = ViewHolder(view)
            view?.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val dateItem = getItem(position)
        holder.mainDateTitle.text = dateItem

        return view!!
    }

    private class ViewHolder(view: View) {
        val mainDateTitle: TextView = view.findViewById(R.id.mainDateTitle)
    }
}