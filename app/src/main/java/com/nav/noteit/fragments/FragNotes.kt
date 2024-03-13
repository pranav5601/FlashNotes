package com.nav.noteit.fragments

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.nav.noteit.R
import com.nav.noteit.adapters.AdapterNotes
import com.nav.noteit.databaseRelations.NoteWithReminder
import com.nav.noteit.databinding.FragNotesBinding
import com.nav.noteit.helper.Utils
import com.nav.noteit.room_models.Note

import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ReminderViewModel
import com.nav.noteit.viewmodel.SearchViewModel
import org.koin.android.ext.android.inject

class FragNotes : FragBase<FragNotesBinding>(), AdapterNotes.ClickListeners,
    PopupMenu.OnMenuItemClickListener {

    private val expandFab: Animation by lazy { AnimationUtils.loadAnimation(baseContext,R.anim.fab_expand_from_bottom) }
    private val shrinkFab: Animation by lazy { AnimationUtils.loadAnimation(baseContext,R.anim.fab_shrink_from_top) }
    private var clicked = false
    private lateinit var adapterNotes: AdapterNotes
    private var noteList = ArrayList<Note>()
    private val noteWithReminderList: ArrayList<NoteWithReminder> by lazy {  ArrayList() }
    private val noteViewModel: NoteViewModel by inject()
    private val reminderViewModel: ReminderViewModel by inject()
    private lateinit var selectedNote : NoteWithReminder
    private val searchViewModel :SearchViewModel by activityViewModels()



    override fun setUpFrag() {

        initViewModel()
        initNoteAdapter()
        initClick()
        filterNote()


    }

    override fun onStart() {
        super.onStart()
        Log.e(FragNotes().tag, "onStart")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(FragNotes().tag, "onAttach")

    }

    private fun filterNote() {
        searchViewModel.selectedItem.observe(viewLifecycleOwner){searchKey ->
            println(searchKey)
            searchViewModel.emptySearch.observe(viewLifecycleOwner) {isEmpty ->

                if (isEmpty) {
                    adapterNotes.updateList(noteWithReminderList)
                } else {
                    adapterNotes.filterList(searchKey)
                }
            }
        }
    }


    private fun initViewModel() {

        noteList.clear()

        noteViewModel.allNotesWithReminder?.let {

            it.observe(viewLifecycleOwner){allDataList ->
                Log.e("all Data",Gson().toJson(allDataList))

                if(allDataList.isEmpty()){
                    binding.notesRcv.visibility = View.GONE
                    binding.txtNoNoteFound.visibility = View.VISIBLE
                }else{
                    binding.notesRcv.visibility = View.VISIBLE
                    binding.txtNoNoteFound.visibility = View.GONE
                }

                noteWithReminderList.addAll(allDataList)
                adapterNotes.updateList(allDataList)

            }
        }

    }


    private fun initNoteAdapter() {

        binding.notesRcv.setHasFixedSize(true)
        binding.notesRcv.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
        adapterNotes = AdapterNotes(baseContext,this)
        binding.notesRcv.adapter = adapterNotes

    }

    override fun onDetach() {
        super.onDetach()
        selectMenuDrawer()
    }


    private fun initClick() {
        binding.btnCreateAction.setOnClickListener {
            changeIconToBack(true)
//            openSubFab()
            Utils.addFrag(baseContext.supportFragmentManager,R.id.mainFragContainer, FragEditNote(), R.anim.slide_in_top, R.anim.slide_out_top, R.anim.slide_in_bottom, R.anim.slide_out_bottom)

        }

        binding.btnCreateNotes.setOnClickListener{



        }

        binding.btnCreateReminder.setOnClickListener {

        }

        binding.notesRcv.setOnClickListener {
            Utils.hideSoftKeyboard(baseContext)
        }
    }



    private fun openSubFab() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            binding.btnCreateNotes.startAnimation(expandFab)
            binding.btnCreateReminder.startAnimation(expandFab)

        }else{
            binding.btnCreateNotes.startAnimation(shrinkFab)
            binding.btnCreateReminder.startAnimation(shrinkFab)

        }

    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked){
            binding.btnCreateNotes.visibility = View.VISIBLE
            binding.btnCreateReminder.visibility = View.VISIBLE
        }else{
            binding.btnCreateNotes.visibility = View.GONE
            binding.btnCreateReminder.visibility = View.GONE
        }
    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragNotesBinding
        get() = FragNotesBinding::inflate

    override fun onItemClicked(note: NoteWithReminder) {
        Utils.addFrag(baseContext.supportFragmentManager,R.id.mainFragContainer,FragEditNote().setInstance(true,note), R.anim.slide_in_top, R.anim.slide_out_top, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
    }

    override fun onLongIteClicked(note: NoteWithReminder, cardView: CardView) {
        selectedNote = note
        popOutNote(cardView)
    }

    private fun popOutNote(cardView: CardView) {
        val popUp = PopupMenu(baseContext,cardView)
        popUp.setOnMenuItemClickListener (this)
        popUp.inflate(R.menu.popup_menu)
        popUp.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.deleteNote){
            noteViewModel.deleteNote(selectedNote.note)
            selectedNote.reminder?.let {
                reminderViewModel.deleteReminder(selectedNote.reminder)
            }
            return true
        }
        return false
    }


}