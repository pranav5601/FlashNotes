package com.nav.noteit.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nav.noteit.R
import com.nav.noteit.activities.ActMain
import com.nav.noteit.adapters.AdapterImageList
import com.nav.noteit.adapters.ReminderViewPagerAdapter
import com.nav.noteit.databinding.FragEditNoteBinding
import com.nav.noteit.helper.Constants
import com.nav.noteit.room_models.ListToStringTypeConverter
import com.nav.noteit.room_models.Note
import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ReminderViewModel
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FragEditNote : FragBase<FragEditNoteBinding>(), ActMain.ClickListeners,
    AdapterImageList.ImageClickListener {

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

    }


    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    private fun initClick() {

        binding.btnAddItems.setOnClickListener {
            openSubFab()

        }

        binding.btnCreateReminder.setOnClickListener {
            openSnackBarForReminder(it)
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
        val customView = layoutInflater.inflate(R.layout.cell_reminder_snackbar, null) as ViewGroup
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
        val btnCustomReminder: ConstraintLayout = customView.findViewById(R.id.lytAddCustomReminder)
        val txtCustomReminder: TextView = customView.findViewById(R.id.txtReminder)
        txtTomorrowReminder.setOnClickListener {
            Toast.makeText(baseContext, "Clicked", Toast.LENGTH_SHORT).show()

        }

        btnCustomReminder.setOnClickListener {

            openReminderDialogueBox()

            /*val reminderDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pick a time")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setPositiveButtonText("Submit")
                .build()
            openCalender(reminderDatePicker, txtCustomReminder)*/
        }


    }

    private fun openReminderDialogueBox() {

        setDialogBox()


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

            val btnPressed = Intent("click").apply { putExtra("clicked",true) }
            LocalBroadcastManager.getInstance(baseContext).sendBroadcast(btnPressed)
            reminderDialog.dismiss()

        }
    }

    private fun setViewPager(reminderDialog: Dialog) {

        val reminderViewPager = reminderDialog.findViewById<ViewPager2>(R.id.reminderViewPager)
        val reminderTabLayout = reminderDialog.findViewById<TabLayout>(R.id.reminderTabLayout)

        val viewPagerAdapter = ReminderViewPagerAdapter(baseContext, 0)

        reminderViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(reminderTabLayout, reminderViewPager) { tab, pos ->

            val tabNames = listOf("Date/Time", "Location")
            tab.text = tabNames[pos]

        }.attach()

    }

    private fun openCalender(
        reminderDatePicker: MaterialDatePicker<Long>,
        txtCustomReminder: TextView
    ) {

        reminderDatePicker.addOnPositiveButtonClickListener { timeMillis ->
            Toast.makeText(baseContext, "Reminder is set.", Toast.LENGTH_SHORT).show()

            val date = Date(timeMillis)
            val simpleDateFormat = SimpleDateFormat("EE, dd MM yyyy", Locale.getDefault())
            val formattedDate = simpleDateFormat.format(date)
            txtCustomReminder.text = formattedDate
        }
        reminderDatePicker.addOnNegativeButtonClickListener {
            Toast.makeText(baseContext, "Reminder Cancelled", Toast.LENGTH_SHORT).show()
        }

        reminderDatePicker.show(baseContext.supportFragmentManager, "Reminder DatePicker")

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
        imgListAdapter = AdapterImageList(baseContext, this)
        imgListAdapter.updateImages(uriList)
        binding.rcvEditImages.setHasFixedSize(true)
        binding.rcvEditImages.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rcvEditImages.adapter = imgListAdapter
    }

    private fun initVars() {

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

            binding.edtTitleNote.setText(oldNote?.title)
            binding.edtNote.setText(oldNote?.description)
            oldNote?.let {

                if (it.imageList.length > 2) {
                    imgDataList.addAll(listToString.stringToList(it.imageList))
                    binding.lytEditNoteImage.visibility = View.VISIBLE
                    setImage(imgDataList)
                }
            }
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
        val type = Constants.noteTypeTxt

        setImage(imgDataList)


        if (validateFields()) {


            if (editNote == true) {
                newNote = Note(
                    title,
                    note,
                    type,
                    timeStamp,
                    listToString.listToString(imgDataList),
                    oldNote?.id!!
                )
                noteViewModel.updateNote(newNote!!)
                baseContext.supportFragmentManager.popBackStackImmediate()

            } else {
                newNote =
                    Note(title, note, type, timeStamp, listToString.listToString(imgDataList), null)
                baseContext.supportFragmentManager.popBackStackImmediate()
                noteViewModel.addNote(newNote!!)

            }
        } else {
            Toast.makeText(baseContext, "Cannot store empty note.", Toast.LENGTH_SHORT).show()
            Log.e("Error", "Cannot store empty note.")
        }

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

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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

//        if(imgDataList.isEmpty()){
//            binding.rcvEditImages.visibility = View.GONE
//        }else{
//            binding.rcvEditImages.visibility = View.VISIBLE
//        }
    }

    fun setInstance(editNote: Boolean, note: Note?): Fragment {
        this.oldNote = note
        this.editNote = editNote
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
        Log.e("image list in string", listToString.listToString(imgDataList))
    }

    override fun onImageClick(imagePos: Int) {
        Toast.makeText(baseContext, "image pressed $imagePos", Toast.LENGTH_SHORT).show()
    }

    override fun onLongImageClick(imagePos: Int) {
        Toast.makeText(baseContext, "image long pressed $imagePos", Toast.LENGTH_SHORT).show()
    }

}


