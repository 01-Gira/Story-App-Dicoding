package com.example.submission1intermediate.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.data.remote.response.ListStoryItem


class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStory(token: String):LiveData<PagingData<ListStoryItem>>{
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }
}