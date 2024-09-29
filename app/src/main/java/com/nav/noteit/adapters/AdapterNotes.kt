package com.nav.noteit.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.nav.noteit.R
import com.nav.noteit.databaseRelations.NoteWithReminder
import com.nav.noteit.helper.Constants
import com.nav.noteit.room_models.ListToStringTypeConverter
import java.util.*

class AdapterNotes(private val context: Context,private val clickListener: ClickListeners, private val userId: String): RecyclerView.Adapter<AdapterNotes.MyViewHolder>() {


    private var notesList =  ArrayList<NoteWithReminder>()
    private var fullList = ArrayList<NoteWithReminder>()



    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val noteTitle = itemView.findViewById<TextView>(R.id.txtNoteTitle)
        private val noteContent = itemView.findViewById<TextView>(R.id.txtNoteContent)
        private val noteTime = itemView.findViewById<TextView>(R.id.txtNoteTime)
        private val lytImgNote = itemView.findViewById<LinearLayout>(R.id.lytNoteImg)
        private val lytTxtNote = itemView.findViewById<LinearLayout>(R.id.lyt_txtNote)
        private val lytMainNote = itemView.findViewById<CardView>(R.id.lytMainNote)
        private val imgNote = itemView.findViewById<ShapeableImageView>(R.id.imgNote)
        private val imgNoteReminder = itemView.findViewById<ShapeableImageView>(R.id.imgNoteReminder)
        var listToStringTypeConverter = ListToStringTypeConverter()



        @SuppressLint("UseCompatLoadingForDrawables")
        fun binding(note: NoteWithReminder, context: Context, clickListener: ClickListeners) {

            noteTitle.text = note.note.title
            noteContent.text = note.note.description
            val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = note.note.timeStamp
            noteTime.text = DateFormat.format("dd-MM-yyyy hh:mm:ss",cal).toString()

            lytMainNote.setOnClickListener {
                clickListener.onItemClicked(note)
            }

            if (note.note.imageList.length > 2){


                val imgList = listToStringTypeConverter.stringToList(note.note.imageList)


                val imgUri = Uri.parse(imgList[0])


                Glide.with(context).load(imgUri).into(imgNote)


            }


            lytMainNote.setOnLongClickListener {
                clickListener.onLongIteClicked(note,lytMainNote)
                true
            }

            if (note.note.isReminderSet){
                Glide.with(context).load(context.resources.getDrawable(R.drawable.bell, null)).into(imgNoteReminder)
            }else{

                Glide.with(context).load(context.resources.getDrawable(R.drawable.circular_note_it_logo, null)).into(imgNoteReminder)
            }

            if(note.note.type == Constants.noteTypeTxt){
                lytTxtNote.visibility = View.VISIBLE
                lytImgNote.visibility = View.GONE
            }else{
                lytImgNote.visibility = View.VISIBLE

            }


        }

    }

    fun updateList(newList: List<NoteWithReminder>){

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
                if (item.note.title.lowercase().contains(search.lowercase()) || item.note.description.lowercase().contains(search.lowercase())){
                    notesList.add(item)
                }
            }

        }else{
            notesList.clear()
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
        fun onItemClicked(note:NoteWithReminder)
        fun onLongIteClicked(note:NoteWithReminder, cardView: CardView)
    }


}