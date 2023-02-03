package com.nav.noteit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nav.noteit.room_models.Note

class ShareNoteViewModel: ViewModel() {

    private val mutableSelectedItem = MutableLiveData<Note>()
    val selectedItem: LiveData<Note> get() = mutableSelectedItem

    fun selectItem(item: Note) {
        mutableSelectedItem.value = item
    }
}