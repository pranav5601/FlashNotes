package com.nav.noteit.fragments

import android.content.Intent.getIntent
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
import com.nav.noteit.models.AlarmItem
import com.nav.noteit.room_models.Note
import com.nav.noteit.room_models.Reminder
import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ReminderAlarmViewModel
import com.nav.noteit.viewmodel.ReminderViewModel
import com.nav.noteit.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class FragNotes : FragBase<FragNotesBinding>(), AdapterNotes.ClickListeners,
    PopupMenu.OnMenuItemClickListener {


    private val expandFab: Animation by lazy {
        AnimationUtils.loadAnimation(
            baseContext,
            R.anim.fab_expand_from_bottom
        )
    }
    private val shrinkFab: Animation by lazy {
        AnimationUtils.loadAnimation(
            baseContext,
            R.anim.fab_shrink_from_top
        )
    }
    private var clicked = false
    private lateinit var adapterNotes: AdapterNotes
    private var noteList = ArrayList<Note>()
    private val noteWithReminderList: ArrayList<NoteWithReminder> by lazy { ArrayList() }
    private val noteViewModel: NoteViewModel by inject()
    private val reminderViewModel: ReminderViewModel by inject()
    private val reminderAlarmViewModel: ReminderAlarmViewModel by inject()
    private lateinit var selectedNote: NoteWithReminder
    private val searchViewModel: SearchViewModel by activityViewModels()

    //    private val userViewModel: UserViewModel by inject()
    private var flag = true


    override fun setUpFrag() {

        if (flag) {

            startFromNotification()
        }

        initNoteAdapter()
        initViewModel()
        initClick()
        filterNote()


    }

    private fun startFromNotification() {
        val notificationId = baseContext.intent.getStringExtra("redirectNote")
        notificationId?.let { id ->
            Log.e("notificationId", id)

            noteViewModel.findNoteById(id).observe(viewLifecycleOwner) { noteData ->
                flag = false

                Utils.addFrag(
                    baseContext.supportFragmentManager,
                    R.id.mainFragContainer,
                    FragEditNote().setInstance(true, noteData),
                    R.anim.slide_in_top,
                    R.anim.slide_out_top,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
                )


            }

        }
    }

    private fun tempRemoteNotes() {

        val note = runBlocking {
            noteViewModel.getAllNotes("")
        }

        noteViewModel.allNoteData.observe(viewLifecycleOwner) {
            Log.e("all notes from cloud", Gson().toJson(it))
        }

    }

    private fun filterNote() {
        searchViewModel.selectedItem.observe(viewLifecycleOwner) { searchKey ->
            println(searchKey)
            searchViewModel.emptySearch.observe(viewLifecycleOwner) { isEmpty ->

                if (isEmpty) {
                    Log.e("noteWithReminder", noteWithReminderList.size.toString())
                    adapterNotes.updateList(noteWithReminderList)
                } else {
                    adapterNotes.filterList(searchKey)
                }
            }
        }
    }


    private fun initViewModel() {


        runBlocking {


            noteViewModel.allNotesWithReminder(getUserIdFromPrefs()!!).let {

                it.observe(viewLifecycleOwner) { allDataList ->
                    Log.e("all Data", Gson().toJson(allDataList))

                    runBlocking {
                        try {
                            async {
                                initReminders(allDataList)
                                checkForUserSpecificData(allDataList)
                            }.await()

                        } catch (e: Exception) {
                            Log.e("init reminder", e.message.toString())
                        }
                    }
                }
            }


        }
    }

    private fun checkForUserSpecificData(allDataList: List<NoteWithReminder>) {


        Log.e("allDataList length", allDataList.size.toString())
        if (allDataList.isEmpty()) {
            binding.notesRcv.visibility = View.GONE
            binding.txtNoNoteFound.visibility = View.VISIBLE
        } else {
            binding.notesRcv.visibility = View.VISIBLE
            binding.txtNoNoteFound.visibility = View.GONE
        }

        noteWithReminderList.clear()
        noteWithReminderList.addAll(allDataList)
        adapterNotes.updateList(noteWithReminderList)
        adapterNotes.notifyDataSetChanged()

    }

    private suspend fun initReminders(allDataList: List<NoteWithReminder>) {

        for (noteWithReminderData in allDataList) {
            val reminderData = noteWithReminderData.reminder
            val noteData = noteWithReminderData.note

            reminderData?.let { reminder ->
                val alarmItem = AlarmItem(
                    reminder.reminderTimestamp,
                    noteData.title,
                    noteData.description,
                    reminderData.reminderRepetition,
                    noteData.noteId,
                    reminder.id
                )

                runBlocking {

                    async {
                        removeSingleOldReminders(reminderData, noteData)
//                        reminderAlarmViewModel.scheduleAlarm(alarmItem)
                    }.await()
                }
            }
        }
    }

    private suspend fun removeSingleOldReminders(reminderData: Reminder?, noteData: Note) {
        reminderData?.let { reminder ->

            if (reminder.reminderRepetition == 0L && reminder.reminderTimestamp < System.currentTimeMillis()) {
                reminderViewModel.removeReminderById(reminder.id)
                getUserIdFromPrefs()?.let { id ->
                    noteViewModel.updateNote(

                        Note(
                            noteData.title,
                            id,   //user id
                            noteData.description,
                            noteData.type,
                            noteData.timeStamp,
                            noteData.imageList,
                            noteData.noteId,
                            false
                        ),

                        )
                }
                adapterNotes.notifyDataSetChanged()
            }

        }
    }


    private fun initNoteAdapter() {

        binding.notesRcv.setHasFixedSize(true)
        binding.notesRcv.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapterNotes = AdapterNotes(baseContext, this, getUserIdFromPrefs()!!)
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
            Utils.addFrag(
                baseContext.supportFragmentManager,
                R.id.mainFragContainer,
                FragEditNote(),
                R.anim.slide_in_top,
                R.anim.slide_out_top,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )

        }

        binding.btnCreateNotes.setOnClickListener {


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
        if (!clicked) {
            binding.btnCreateNotes.startAnimation(expandFab)
            binding.btnCreateReminder.startAnimation(expandFab)

        } else {
            binding.btnCreateNotes.startAnimation(shrinkFab)
            binding.btnCreateReminder.startAnimation(shrinkFab)

        }

    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.btnCreateNotes.visibility = View.VISIBLE
            binding.btnCreateReminder.visibility = View.VISIBLE
        } else {
            binding.btnCreateNotes.visibility = View.GONE
            binding.btnCreateReminder.visibility = View.GONE
        }
    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragNotesBinding
        get() = FragNotesBinding::inflate

    override fun onItemClicked(note: NoteWithReminder) {
        Utils.addFrag(
            baseContext.supportFragmentManager,
            R.id.mainFragContainer,
            FragEditNote().setInstance(true, note),
            R.anim.slide_in_top,
            R.anim.slide_out_top,
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom
        )
    }

    override fun onLongIteClicked(note: NoteWithReminder, cardView: CardView) {
        selectedNote = note
        popOutNote(cardView)
    }

    private fun popOutNote(cardView: CardView) {
        val popUp = PopupMenu(baseContext, cardView)
        popUp.setOnMenuItemClickListener(this)
        popUp.inflate(R.menu.popup_menu)
        popUp.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.deleteNote) {

            runBlocking(Dispatchers.IO) {

                val result = listOf(
                    async {
                        noteViewModel.deleteNote(selectedNote.note)

                    },
                    async {

                        selectedNote.reminder?.let {
                            reminderViewModel.deleteReminder(selectedNote.reminder)
                            val alarmItem = AlarmItem(
                                it.reminderTimestamp,
                                selectedNote.note.title,
                                selectedNote.note.description,
                                it.reminderRepetition,
                                it.noteId,
                                it.id
                            )
                            reminderAlarmViewModel.cancelAlarm(alarmItem)
                        }
                    }
                )

                result.awaitAll()

                noteViewModel.allNotesWithReminder(getUserIdFromPrefs()!!)

            }
            return true
        }
        return false
    }


}