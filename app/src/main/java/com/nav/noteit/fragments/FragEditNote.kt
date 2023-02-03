package com.nav.noteit.fragments

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nav.noteit.activities.ActMain
import com.nav.noteit.databinding.FragEditNoteBinding
import com.nav.noteit.helper.Constants
import com.nav.noteit.room_models.Note
import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ShareNoteViewModel
import org.koin.android.ext.android.inject


class FragEditNote : FragBase<FragEditNoteBinding>(), ActMain.ClickListeners {

    private var newNote: Note? = null
    private var oldNote: Note? = null
    private var editNote: Boolean? = false
private val noteViewModel by inject<NoteViewModel>()
    lateinit var noteTitle: String


    override fun setUpFrag() {

        initVars()
        initView()
        setData()

    }



    private fun initVars() {
    }

    private fun setData() {

        if (oldNote != null) {
            binding.edtTitleNote.setText(oldNote?.title)
            binding.edtNote.setText(oldNote?.description)
        }


    }

    private fun initView() {


    }



    private fun insertNote() {

        val title = binding.edtTitleNote.text.toString()
        val note = binding.edtNote.text.toString()
        val timeStamp = System.currentTimeMillis()
        val type = Constants.noteTypeTxt


        if (validateFields()) {


            if (editNote == true) {
                newNote = Note(title, note, type, timeStamp, oldNote?.id!!)
                noteViewModel.updateNote(newNote!!)
                baseContext.supportFragmentManager.popBackStackImmediate()

            } else {
                newNote = Note(title, note, type, timeStamp, null)
                baseContext.supportFragmentManager.popBackStackImmediate()
                noteViewModel.addNote(newNote!!)

            }
        } else {
            Log.e("Error","Cannot store empty note.")
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        changeToSaveIcon(true, this)
        changeIconToBack(true)

    }

    fun setInstance(editNote: Boolean, note: Note?): Fragment {
        this.oldNote = note
        this.editNote = editNote
        return this
    }

    override fun onDetach() {
        super.onDetach()
        changeIconToBack(false)
        changeToSaveIcon(false, this)
    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragEditNoteBinding
        get() = FragEditNoteBinding::inflate

    fun validateFields(): Boolean {
        return if (binding.edtTitleNote.text == null && binding.edtTitleNote.text.isEmpty()) {
            false
        } else !(binding.edtNote.text == null && binding.edtNote.text.isEmpty())
    }

    override fun onSaveBtnClick() {

        insertNote()
        println("Pressed")
    }

}