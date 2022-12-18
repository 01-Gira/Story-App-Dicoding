package com.example.submission1intermediate.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.example.submission1intermediate.data.local.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getResponselogin(email: String, pass: String) =
        storyRepository.getResponseLogin(email, pass)

}