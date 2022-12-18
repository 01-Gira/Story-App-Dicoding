package com.example.submission1intermediate.ui.home.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submission1intermediate.data.local.data.StoryRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val storyRepository: StoryRepository) : ViewModel(){

    fun isLogin():LiveData<Boolean> = storyRepository.isLogin()


    fun loginState(){
        viewModelScope.launch {
            storyRepository.loginState()
        }
    }

    fun saveToken(token:String,name:String){
        viewModelScope.launch {
            storyRepository.saveToken(token,name)
        }
    }

    fun getToken(): LiveData<String> = storyRepository.getToken()

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }

}