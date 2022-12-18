package com.example.submission1intermediate.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.example.submission1intermediate.data.local.data.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getResponseRegister(name: String, email: String, pass: String) =
        storyRepository.getResponseRegister(name, email, pass)

}