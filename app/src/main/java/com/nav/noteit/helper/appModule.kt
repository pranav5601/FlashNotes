package com.nav.noteit.helper

import androidx.room.Room
import com.nav.noteit.adapters.AdapterImageList
import com.nav.noteit.database.NoteDatabase
import com.nav.noteit.repositories.NoteRepo
import com.nav.noteit.room_models.ListToStringTypeConverter
import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ReminderViewModel
import com.nav.noteit.viewmodel.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module{


    single {
        Room.databaseBuilder(get(),NoteDatabase::class.java,"note_database").fallbackToDestructiveMigration().build()
    }


    single  {
        NoteRepo(get())
    }
    viewModel {
        NoteViewModel(get())
    }

    single {
        ListToStringTypeConverter()
    }

    viewModel{
        ReminderViewModel()
    }



}