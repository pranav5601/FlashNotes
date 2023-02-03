package com.nav.noteit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {


    private val mutableSelectedItem = MutableLiveData<String>()
    val selectedItem: LiveData<String> get() = mutableSelectedItem

    private val mutableEmptySearch = MutableLiveData<Boolean>()
    val emptySearch: LiveData<Boolean> get() = mutableEmptySearch

    fun selectItem(item: String) {
        mutableSelectedItem.value = item
    }

    fun emptySearch(isEmpty: Boolean){
        mutableEmptySearch.value = isEmpty
    }
}