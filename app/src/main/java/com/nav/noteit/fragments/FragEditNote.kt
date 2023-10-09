package com.nav.noteit.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nav.noteit.activities.ActMain
import com.nav.noteit.adapters.AdapterImageList
import com.nav.noteit.databinding.FragEditNoteBinding
import com.nav.noteit.helper.Constants
import com.nav.noteit.room_models.ListToStringTypeConverter
import com.nav.noteit.room_models.Note
import com.nav.noteit.viewmodel.NoteViewModel
import org.koin.android.ext.android.inject
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor


class FragEditNote : FragBase<FragEditNoteBinding>(), ActMain.ClickListeners,
    AdapterImageList.ImageClickListener {

    private var newNote: Note? = null
    private var oldNote: Note? = null
    private var editNote: Boolean? = false
    private val noteViewModel by inject<NoteViewModel>()
    private val listToString by inject<ListToStringTypeConverter>()
    lateinit var noteTitle: String
    private var finalString: StringBuffer = StringBuffer()
    private var imgList: ArrayList<Uri> = ArrayList<Uri>()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val REQUEST_CODE_OPEN_DOCUMENT = 100
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var imgListAdapter: AdapterImageList
    private val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
    private val TAG = "ImageSelectionActivity"
    private lateinit var imgDataList: ArrayList<String>
    private var imgString: String = ""

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
        binding.fabAddPhotos.setOnClickListener {

            if (isPhotoPickerAvailable()) {

                showImagePicker()
            } else {
                checkAndRequestPermission()
            }

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

                if (it.imageList.isNotEmpty()) {
                    imgDataList.addAll(listToString.stringToList(it.imageList))
                    binding.lytEditNoteImage.visibility = View.VISIBLE
                    setImage(imgDataList)
                }
            }
        }


    }

    private fun initView() {


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