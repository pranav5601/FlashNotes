package com.nav.noteit.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.nav.noteit.R
import com.nav.noteit.fragments.FragNotes
import com.nav.noteit.helper.Constants
import com.nav.noteit.room_models.Note
import java.util.*

class AdapterNotes(private val context: Context,private val clickListener: ClickListeners): RecyclerView.Adapter<AdapterNotes.MyViewHolder>() {


    var notesList =  ArrayList<Note>()
    var fullList = ArrayList<Note>()


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val noteTitle = itemView.findViewById<TextView>(R.id.txtNoteTitle)
        private val noteContent = itemView.findViewById<TextView>(R.id.txtNoteContent)
        private val noteTime = itemView.findViewById<TextView>(R.id.txtNoteTime)
        private val lytImgNote = itemView.findViewById<LinearLayout>(R.id.lytNoteImg)
        private val lytTxtNote = itemView.findViewById<LinearLayout>(R.id.lyt_txtNote)
        private val lytMainNote = itemView.findViewById<CardView>(R.id.lytMainNote)
        private val imgNote = itemView.findViewById<ShapeableImageView>(R.id.imgNote)


        fun binding(note: Note, context: Context, clickListener: ClickListeners) {

            noteTitle.text = note.title
            noteContent.text = note.description
            val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = note.timeStamp
            noteTime.text = DateFormat.format("dd-MM-yyyy hh:mm:ss",cal).toString()

            lytMainNote.setOnClickListener {
                clickListener.onItemClicked(note)
            }

            lytMainNote.setOnLongClickListener {
                clickListener.onLongIteClicked(note,lytMainNote)
                true
            }

            if(note.type == Constants.noteTypeTxt){
                lytTxtNote.visibility = View.VISIBLE
                lytImgNote.visibility = View.GONE
            }else{
                lytTxtNote.visibility = View.GONE
                lytImgNote.visibility = View.VISIBLE

            }


        }

    }

    fun updateList(newList: List<Note>){

        fullList.clear()
        fullList.addAll(newList)

        notesList.clear()
        notesList.addAll(fullList)
        notifyDataSetChanged()

    }

    fun filterList(search: String){
        notesList.clear()
        if(search.isNotEmpty()){
            for (item in fullList){
                if (item.title.lowercase().contains(search.lowercase()) || item.description.lowercase().contains(search.lowercase())){
                    notesList.add(item)
                }
            }

        }else{
            notesList.addAll(fullList)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.cell_note_list,parent,false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int = notesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding(notesList[position], context, clickListener)

    }

    interface ClickListeners {
        fun onItemClicked(note:Note)
        fun onLongIteClicked(note:Note, cardView: CardView)
    }


}