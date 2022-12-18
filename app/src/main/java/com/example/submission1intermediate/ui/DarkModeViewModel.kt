package com.example.submission1intermediate.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission1intermediate.data.local.data.StoryRepository
import kotlinx.coroutines.launch

class DarkModeViewModel(private val userRepository: StoryRepository): ViewModel(){

    fun getThemeMode():LiveData<Boolean> = userRepository.getThemeMode()

    fun saveThemeMode(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            userRepository.saveThemeMode(isDarkModeActive)
        }
    }
}