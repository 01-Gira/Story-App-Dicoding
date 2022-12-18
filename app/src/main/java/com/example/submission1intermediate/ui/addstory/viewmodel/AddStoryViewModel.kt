package com.example.submission1intermediate.ui.addstory.viewmodel

import androidx.lifecycle.ViewModel
import com.example.submission1intermediate.data.local.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun upload(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ) = storyRepository.upload(token,file,description)

}