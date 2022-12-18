package com.example.submission1intermediate.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submission1intermediate.data.di.Injection
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.ui.addstory.viewmodel.AddStoryViewModel
import com.example.submission1intermediate.ui.auth.viewmodel.LoginViewModel
import com.example.submission1intermediate.ui.auth.viewmodel.RegisterViewModel
import com.example.submission1intermediate.ui.home.viewmodel.AuthViewModel
import com.example.submission1intermediate.ui.home.viewmodel.MainViewModel
import com.example.submission1intermediate.ui.map.viewmodel.MapsViewModel


class ViewModelFactory(private val pref: UserPreference, private val context: Context): ViewModelProvider.NewInstanceFactory() {
    @Suppress("ÜNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return AddStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(DarkModeViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                DarkModeViewModel(Injection.provideRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

}