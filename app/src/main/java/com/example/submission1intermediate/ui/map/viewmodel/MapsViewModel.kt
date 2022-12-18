package com.example.submission1intermediate.ui.map.viewmodel

import androidx.lifecycle.ViewModel
import com.example.submission1intermediate.data.local.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(token: String) =
        storyRepository.getStories(token)

}