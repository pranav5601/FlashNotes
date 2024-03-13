package com.nav.noteit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.nav.noteit.R
import com.nav.noteit.models.RepetitionItem

class CustomRepetitionAdapter(private val context: Context, private val listOfRepetition: List<String>) :BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = listOfRepetition.size


    override fun getItem(pos: Int): String {
       return  listOfRepetition[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.cell_repetition_menu, null)
            holder = ViewHolder(view)
            view?.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val repeatItem = getItem(position)
        holder.mainRepeatTitle.text = repeatItem

        return view!!
    }

    private class ViewHolder(view: View) {
        val mainRepeatTitle: TextView = view.findViewById(R.id.mainRepeatTitle)
    }
}