package com.nav.noteit.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.nav.noteit.R
import com.nav.noteit.activities.ActMain
import com.nav.noteit.adapters.AdapterImageList
import com.nav.noteit.adapters.ReminderViewPagerAdapter
import com.nav.noteit.databaseRelations.NoteWithReminder
import com.nav.noteit.databinding.FragEditNoteBinding
import com.nav.noteit.helper.Constants
import com.nav.noteit.helper.Utils
import com.nav.noteit.models.AlarmItem
import com.nav.noteit.room_models.ListToStringTypeConverter
import com.nav.noteit.room_models.Note
import com.nav.noteit.room_models.Reminder
import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ReminderAlarmViewModel
import com.nav.noteit.viewmodel.ReminderViewModel
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FragEditNote : FragBase<FragEditNoteBinding>(), ActMain.ClickListeners,
    AdapterImageList.ImageClickListener {

    private lateinit var mNotificationManager: NotificationManager
    private var reminderId: Int? = null
    private var newNote: Note? = null
    private var oldNote: Note? = null
    private var editNote: Boolean? = false
    private val noteViewModel by inject<NoteViewModel>()
    private val listToString by inject<ListToStringTypeConverter>()
    private val reminderViewModel by inject<ReminderViewModel>()
    private var finalString: StringBuffer = StringBuffer()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imgListAdapter: AdapterImageList
    private val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
    private val TAG = "ImageSelectionActivity"
    private lateinit var imgDataList: ArrayList<String>
    private var imgString: String = ""
    private var clicked = false
    private var backPressedTime: Long = 0
    private lateinit var reminderDialog: Dialog
    private var noteId: Int = 0
    private var reminderData: Reminder? = null
    private var reminderAlarmManager: AlarmManager? = null
    private var isAlarmSet: Boolean = false
    private var type: String = Constants.noteTypeTxt
    private val alarmReminderViewModel by inject<ReminderAlarmViewModel>()
    private var time = 0L
    private lateinit var reminderCalendar: Calendar


    //animation
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

    companion object {
        lateinit var snackBarReminder: Snackbar
        var toHideBgLayout: Boolean =
            false    //true for snackBar layout and false for floating button
    }

    override fun setUpFrag() {

        initVars()
        initView()
        initClick()
        setData()
        receiveReminderDataFromPicker()

    }

    private fun setReminderData(hour: Int) {
        reminderCalendar = Calendar.getInstance()
        val currentDate = reminderCalendar.get(Calendar.DATE)
        reminderCalendar.add(Calendar.DAY_OF_MONTH, 1)
        reminderCalendar.set(
            reminderCalendar.get(Calendar.YEAR),
            reminderCalendar.get(Calendar.MONTH),
            reminderCalendar.get(Calendar.DAY_OF_MONTH),
            hour,
            0,
            0
        )
        time = reminderCalendar.timeInMillis

        val currentTimeFormatForDate =
            SimpleDateFormat("LLLL dd", Locale.getDefault())

        val currentTimeFormatForTime =
            SimpleDateFormat("KK:mm aaa", Locale.getDefault())


        val txtDate = currentTimeFormatForDate.format(time)
        val txtTime = currentTimeFormatForTime.format(time)
        Toast.makeText(baseContext, txtTime, Toast.LENGTH_SHORT).show()


        isAlarmSet = true

        reminderData = Reminder(
            null,
            noteId,
            binding.edtTitleNote.text.toString(),
            getUserIdFromPrefs()!!,
            txtTime,
            txtDate,
            0L,
            reminderCalendar.timeInMillis,
            0L,
            0L
        )
        setReminderTile(time, 0L)
        snackBarReminder.dismiss()
        showBlurBg(false)


    }


    private fun setReminderTile(reminderTimestamp: Long?, reminderRepetition: Long?) {

        binding.lytFinalReminderTime.visibility = View.VISIBLE


        if (reminderRepetition != 0L) {
            binding.reminderIcon.setPadding(8)
            Glide.with(baseContext)
                .load(AppCompatResources.getDrawable(baseContext, R.drawable.repeat))
                .into(binding.reminderIcon)
        } else {

            Glide.with(baseContext)
                .load(AppCompatResources.getDrawable(baseContext, R.drawable.reminder_asset))
                .into(binding.reminderIcon)
        }

        val currentTimeFormat =
            SimpleDateFormat("LLLL dd, hh:mm aaa", Locale.getDefault())

        val txtTime = currentTimeFormat.format(reminderTimestamp)
        Log.e("txtTime", txtTime)

        binding.txtSelectedTime.text = txtTime

    }


    private fun receiveReminderDataFromPicker() {


        setFragmentResultListener(Constants.REQUEST_REMINDER_KEY) { requestKey, bundle ->

            bundle.getString(Constants.REMINDER_DATA_KEY)?.let { data ->

                reminderData = Gson().fromJson(data, Reminder::class.java)
                isAlarmSet = true

                Log.e("reminderData", data)
                setReminderTile(reminderData?.reminderTimestamp, reminderData?.reminderRepetition)
            }

        }

    }


    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    private fun initClick() {

        binding.btnAddItems.setOnClickListener {
            openSubFab()
            Utils.hideSoftKeyboard(baseContext)

        }

        binding.btnCreateReminder.setOnClickListener {
            openSnackBarForReminder(it)
        }

        binding.btnOpenReminder.setOnClickListener {

            setDialogBox()
        }

        binding.btnAddImages.setOnClickListener {

            if (isPhotoPickerAvailable()) {

                showImagePicker()
            } else {
                checkAndRequestPermission()
            }

        }
        binding.windowBlurBg.setOnClickListener {


            if (toHideBgLayout && snackBarReminder.isShown) {
                it.visibility = View.GONE
                snackBarReminder.dismiss()
            } else {
                openSubFab()
            }

        }
    }


    @SuppressLint("RestrictedApi")
    private fun openSnackBarForReminder(view: View) {

        snackBarReminder = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        openSubFab()
        val customView =
            layoutInflater.inflate(R.layout.cell_reminder_snackbar, null) as ViewGroup
        snackBarReminder.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout: SnackbarLayout = snackBarReminder.view as SnackbarLayout
        snackbarInternalClick(customView)

        snackbarLayout.setPadding(2, 2, 2, 2)
        snackbarLayout.addView(customView, 0)
        showBlurBg(!clicked)
        setSnackbarVisibility(snackBarReminder)
    }

    private fun setSnackbarVisibility(snackBarReminder: Snackbar) {
        if (!snackBarReminder.isShown) {
            toHideBgLayout = true
            snackBarReminder.show()
        }
    }

    private fun snackbarInternalClick(customView: ViewGroup) {
        val txtTomorrowReminder: ConstraintLayout =
            customView.findViewById(R.id.lytTomorrowMorningReminder)

        val txtTomorrowEveReminder: ConstraintLayout =
            customView.findViewById(R.id.lytTomorrowEveningReminder)
        val btnCustomReminder: ConstraintLayout =
            customView.findViewById(R.id.lytAddCustomReminder)
        val txtCustomReminder: TextView = customView.findViewById(R.id.txtReminder)
        txtTomorrowReminder.setOnClickListener {
            setReminderData(8)
//            reminderData = Reminder(null, noteId, binding.edtTitleNote.text.toString(), getUserIdFromPrefs()!!, reminderCalendar.timeInMillis)
        }

        txtTomorrowEveReminder.setOnClickListener {
            setReminderData(18)
        }

        btnCustomReminder.setOnClickListener {

            setDialogBox()

        }


    }


    private fun setDialogBox() {
        reminderDialog = Dialog(baseContext)
        reminderDialog.setContentView(layoutInflater.inflate(R.layout.reminder_alert_box, null))

        val displayMetrics = DisplayMetrics()

        baseContext.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val reducedWidth = (width * 9) / 100

        reminderDialog.window?.setLayout(
            width - reducedWidth.toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        reminderDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        reminderDialog.show()

        setViewPager(reminderDialog)
        setUpClicks(reminderDialog)
    }

    private fun setUpClicks(reminderDialog: Dialog) {
        val btnCancel: TextView = reminderDialog.findViewById(R.id.btnCancel)
        val btnSave: Button = reminderDialog.findViewById(R.id.btnSaveReminder)

        btnCancel.setOnClickListener {
            reminderDialog.dismiss()
        }

        btnSave.setOnClickListener {


            setFragmentResult("requestClick", bundleOf("note_id" to noteId))

            reminderDialog.dismiss()


        }
    }

    private fun setViewPager(reminderDialog: Dialog) {

        val reminderViewPager = reminderDialog.findViewById<ViewPager2>(R.id.reminderViewPager)
        val reminderTabLayout = reminderDialog.findViewById<TabLayout>(R.id.reminderTabLayout)

        val viewPagerAdapter = ReminderViewPagerAdapter(baseContext, 0, reminderData)

        reminderViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(reminderTabLayout, reminderViewPager) { tab, pos ->

            val tabNames = listOf(getString(R.string.date_time), getString(R.string.location))
            tab.text = tabNames[pos]

        }.attach()

    }


    private fun openSubFab() {
        setVisibility(clicked)
        showBlurBg(!clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun showBlurBg(setVisible: Boolean) {

        binding.windowBlurBg.visibility = if (setVisible) View.VISIBLE else View.GONE

    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.btnAddImages.startAnimation(expandFab)
            binding.btnCreateReminder.startAnimation(expandFab)

        } else {
            binding.btnAddImages.startAnimation(shrinkFab)
            binding.btnCreateReminder.startAnimation(shrinkFab)

        }

    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.btnAddImages.visibility = View.VISIBLE
            binding.btnCreateReminder.visibility = View.VISIBLE
        } else {
            binding.btnAddImages.visibility = View.GONE
            binding.btnCreateReminder.visibility = View.GONE
        }
    }

    private fun showImagePicker() {

        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }


    private fun setImage(uriList: ArrayList<String>) {
        type = Constants.noteTypeImgPlusTxt
        imgListAdapter = AdapterImageList(baseContext, this)
        imgListAdapter.updateImages(uriList)
        binding.rcvEditImages.setHasFixedSize(true)
        binding.rcvEditImages.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rcvEditImages.adapter = imgListAdapter
    }

    private fun initVars() {

        mNotificationManager =
            baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        imgDataList = ArrayList<String>()
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openImagePicker()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Permission denied. Cannot access images.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    private fun setData() {

        if (oldNote != null) {
            finalString.append(oldNote?.description!!)
            isAlarmSet = oldNote?.isReminderSet!!
            oldNote?.let { noteId = it.noteId }

            binding.edtTitleNote.setText(oldNote?.title)
            binding.edtNote.setText(oldNote?.description)
            oldNote?.let {

                if (it.imageList.length > 2) {
                    imgDataList.addAll(listToString.stringToList(it.imageList))
                    binding.lytEditNoteImage.visibility = View.VISIBLE
                    setImage(imgDataList)
                }
            }

            if (reminderData?.id != null) {

                binding.lytFinalReminderTime.visibility = View.VISIBLE

                val currentTimeFormat =
                    SimpleDateFormat("LLLL dd, hh:mm aaa", Locale.getDefault())

                val txtTime = currentTimeFormat.format(reminderData?.reminderTimestamp)

                if (reminderData?.reminderRepetition != 0L) {
                    binding.reminderIcon.setPadding(8)
                    Glide.with(baseContext)
                        .load(AppCompatResources.getDrawable(baseContext, R.drawable.repeat))
                        .into(binding.reminderIcon)
                } else {

                    Glide.with(baseContext)
                        .load(AppCompatResources.getDrawable(baseContext, R.drawable.reminder_asset))
                        .into(binding.reminderIcon)
                }

                binding.txtSelectedTime.text = txtTime
            } else {

                binding.lytFinalReminderTime.visibility = View.GONE
            }
        } else {
            noteId = Utils.getNoteId()
        }


    }

    private fun initView() {
        val rootView = binding.root
        initBackClick(rootView)
    }

    private fun initBackClick(rootView: View) {
        rootView.isFocusableInTouchMode = true
        rootView.requestFocus()
        rootView.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                handleBackButtonPress()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun handleBackButtonPress() {
        if (toHideBgLayout && snackBarReminder.isShown) {
            snackBarReminder.dismiss()
        } else {

            baseContext.supportFragmentManager.popBackStack()
        }
    }

    private fun handleFragmentBackNavigation() {
        // Implement your fragment back navigation logic here
        if (System.currentTimeMillis() - backPressedTime > 2000) {
            // Example: Display a toast message
            // Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
            backPressedTime = System.currentTimeMillis()
        } else {
            // Example: Navigate back

        }
    }


    private fun insertNote() {

        val title = binding.edtTitleNote.text.toString()
        val note = binding.edtNote.text.toString()
        val timeStamp = System.currentTimeMillis()
        type = Constants.noteTypeTxt


        setImage(imgDataList)


        showLoader()

        if (validateFields()) {


            runBlocking {
                setReminder(title, note)
                setNote(title, note, timeStamp)
//                noteViewModel.allNotesWithReminder(getUserIdFromPrefs()!!)
            }
            closeLoader()
            baseContext.supportFragmentManager.popBackStackImmediate()


        } else {
            Toast.makeText(baseContext, "Cannot store empty note.", Toast.LENGTH_SHORT).show()
            Log.e("Error", "Cannot store empty note.")
        }

    }

    private suspend fun setNote(
        title: String,
        note: String,
        timeStamp: Long,

        ) {

        type = if (imgDataList.isNotEmpty()) {
            Constants.noteTypeImgPlusTxt
        } else {
            Constants.noteTypeTxt
        }

        if (editNote == true) {
            newNote = Note(
                title,
                getUserIdFromPrefs()!!,
                note,
                type,
                timeStamp,
                listToString.listToString(imgDataList),
                noteId,
                isAlarmSet,
            )
            noteViewModel.updateNote(newNote!!)


        } else {
            newNote =
                Note(
                    title,
                    getUserIdFromPrefs()!!,
                    note,
                    type,
                    timeStamp,
                    listToString.listToString(imgDataList),
                    noteId,
                    isAlarmSet
                )

            noteViewModel.addNote(newNote!!)
            closeLoader()
//                    noteViewModel.createNoteOnline(com.nav.noteit.models.Note(noteId,title,timeStamp,type,note,isAlarmSet,listToString.listToString(imgDataList)))

        }


    }

    private fun setReminder(
        title: String,
        note: String,
    ) {
        reminderData?.let { reminderData ->

            reminderData.title = title

            reminderData.id = reminderId

            if (reminderData.id != null) {
                Log.e("reminderId", reminderData.title.toString())

                reminderViewModel.updateReminder(reminderData)

            } else {
                reminderViewModel.insertReminder(reminderData)
            }

            setReminderAlarm(reminderData, title, note)

        }

    }


    private fun setReminderAlarm(reminderData: Reminder, title: String, note: String) {

        val alarmItem = AlarmItem(
            reminderData.reminderTimestamp,
            reminderData.title!!,
            note,
            reminderData.reminderRepetition,
            noteId,
            reminderData.id
        )

        alarmReminderViewModel.scheduleAlarm(alarmItem)




        Log.e("reminderData", Gson().toJson(reminderData))

    }

    private fun checkAndRequestPermission() {
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                baseContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openImagePicker()
        } else {
            // Request the permission if not granted
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    if (data.clipData != null) {
                        // Multiple images selected
                        val clipData = data.clipData
                        for (i in 0 until clipData!!.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            imgDataList.add(uri.toString())
                            setImage(imgDataList)
                            binding.lytEditNoteImage.visibility = View.VISIBLE
                        }
                    } else if (data.data != null) {

                        // Single image selected
                        val uri = data.data
                        binding.lytEditNoteImage.visibility = View.VISIBLE
                        imgDataList.add(uri.toString())
                        setImage(imgDataList)

                    }

                    // You now have the list of selected image URIs in selectedImageUris
                    selectedImageUris.forEach { uri ->
                        Log.i(TAG, uri.path!!)
                    }
                    openSubFab()
                }
            }
        }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                println("Permission has been granted by user")
            }

            false -> {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    baseContext.contentResolver.takePersistableUriPermission(uri, flag)

//                noteBitMap?.let { imgString = imgBitmapString.bitmapToString(it) }

                    uri.let { imgDataList.add(uri.toString()) }

                    Log.e("image bitmap: ", imgString)

                    binding.lytEditNoteImage.visibility = View.VISIBLE
                    setImage(imgDataList)

                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        changeToSaveIcon(true, this)
        changeIconToBack(true)

    }

    fun setInstance(editNote: Boolean, note: NoteWithReminder?): Fragment {
        this.oldNote = note?.note
        this.reminderId = note?.reminder?.id
        this.editNote = editNote
        this.reminderData = note?.reminder
        Log.e("reminder ID", note?.reminder?.id.toString())
        return this
    }


    override fun onDetach() {
        super.onDetach()

        if (toHideBgLayout && snackBarReminder.isShown) {
            snackBarReminder.dismiss()
        }
        changeIconToBack(false)
        changeToSaveIcon(false, this)
    }


    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragEditNoteBinding
        get() = FragEditNoteBinding::inflate

    private fun validateFields(): Boolean {
        return if (binding.edtTitleNote.text == null || binding.edtTitleNote.text.isEmpty()) {
            false
        } else !(binding.edtNote.text == null || binding.edtNote.text.isEmpty())
    }

    override fun onSaveBtnClick() {

        insertNote()
    }

    override fun onImageClick(imagePos: Int) {
        Toast.makeText(baseContext, "image pressed $imagePos", Toast.LENGTH_SHORT).show()
    }

    override fun onLongImageClick(imagePos: Int) {
        Toast.makeText(baseContext, "image long pressed $imagePos", Toast.LENGTH_SHORT).show()
    }

}


