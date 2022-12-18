package com.example.submission1intermediate.data.local.data


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.submission1intermediate.data.local.database.StoryDatabase
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.data.remote.response.*
import com.example.submission1intermediate.data.remote.retrofit.ApiService
import com.example.submission1intermediate.data.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token:String):LiveData<PagingData<ListStoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStories(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getMapStories("Bearer $token")
                if (!response.error) {
                    emit(Result.Success(response.listStory))
                } else {
                    Log.e(TAG, "GetStoryMap Fail: ${response.message}")
                    emit(Result.Error(response.message))
                }

            } catch (e: Exception) {
                Log.e(TAG, "GetStoryMap Exception: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getResponseLogin(email: String, pass: String): LiveData<Result<LoginResult>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.login(email, pass)
                if (!response.error) {
                    emit(Result.Success(response.loginResult))
                } else {
                    Log.e(TAG, "Register Fail: ${response.message}")
                    emit(Result.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getResponseRegister(name: String, email: String, pass: String): LiveData<Result<RegisterResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, pass)
                if (!response.error) {
                    emit(Result.Success(response))
                } else {
                    Log.e(TAG, "Register Fail: ${response.message}")
                    emit(Result.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun upload(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody

    ):LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage("Bearer $token", file, description)
            if (!response.error) {
                emit(Result.Success(response))
            } else {
                Log.e(TAG, "PostStory Fail: ${response.message}")
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "PostStory Exception: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun isLogin():LiveData<Boolean> = userPreference.isLogin().asLiveData()

    suspend fun loginState() {
        userPreference.login()
    }

    suspend fun saveToken(token: String, name: String) {
        userPreference.saveInfoUser(token, name)
    }

    fun getToken(): LiveData<String> = userPreference.getToken().asLiveData()

    suspend fun logout() {
        userPreference.logout()
    }

    fun getThemeMode(): LiveData<Boolean> = userPreference.getThemeSetting().asLiveData()

    suspend fun saveThemeMode(isDarkModeActive: Boolean) {
        userPreference.saveThemeSetting(isDarkModeActive)
    }

    companion object {
        private const val TAG = "StoryRepository"
    }
}