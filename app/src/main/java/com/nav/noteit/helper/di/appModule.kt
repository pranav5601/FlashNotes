package com.nav.noteit.helper.di

import androidx.room.Room
import com.nav.noteit.database.NoteDatabase
import com.nav.noteit.remote.ApiInterface
import com.nav.noteit.remote.NoteListRepoImpl
import com.nav.noteit.remote.UserRepoImpl
import com.nav.noteit.remote.local_data.UserPrefRepoImpl
import com.nav.noteit.remote.local_data.UserPreferences
import com.nav.noteit.remote.repositories.NoteListRepo
import com.nav.noteit.remote.repositories.UserPrefRepo
import com.nav.noteit.remote.repositories.UserRemoteRepo
import com.nav.noteit.repositories.AlarmRepoImpl
import com.nav.noteit.repositories.NoteRepo
import com.nav.noteit.repositories.ReminderRepo
import com.nav.noteit.room_models.ListToStringTypeConverter
import com.nav.noteit.viewmodel.NoteViewModel
import com.nav.noteit.viewmodel.ReminderAlarmViewModel
import com.nav.noteit.viewmodel.ReminderViewModel
import com.nav.noteit.viewmodel.UserViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



val appModule = module {




    single {
        Room.databaseBuilder(get(), NoteDatabase::class.java, "note_database")
            .fallbackToDestructiveMigration().build()
    }


    factory {
        UserPreferences(get())
    }

    factory<NoteListRepo> {
        NoteListRepoImpl(get(), get())
    }

    factory <UserPrefRepo>{
        UserPrefRepoImpl(get())
    }


    factory<UserRemoteRepo> {
        UserRepoImpl(get())
    }

    factory {
        NoteRepo(get())
    }

    factory {
        AlarmRepoImpl(get())
    }

    factory {
        ReminderRepo(get())
    }

    factory {
        ListToStringTypeConverter()
    }

    viewModel {
        UserViewModel(get(), get())
    }

    viewModel {
        NoteViewModel(get(), get())
    }

    viewModel {
        ReminderViewModel(get())
    }

    viewModel {
        ReminderAlarmViewModel(get())
    }


}