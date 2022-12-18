package com.example.submission1intermediate.data.remote.retrofit

import com.example.submission1intermediate.DataDummy
import com.example.submission1intermediate.data.remote.response.FileUploadResponse
import com.example.submission1intermediate.data.remote.response.LoginResponse
import com.example.submission1intermediate.data.remote.response.RegisterResponse
import com.example.submission1intermediate.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    private val dummyStoryResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyFileUploadResponse = DataDummy.generateDummyFileUploadResponse()

    override suspend fun register(name: String, email: String, pass: String): RegisterResponse {
        return dummyRegisterResponse
    }

    override suspend fun login(email: String, pass: String): LoginResponse {
        return dummyLoginResponse
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody

    ): FileUploadResponse {
        return dummyFileUploadResponse
    }

    override suspend fun getStories(
        token: String,
        page: Int,
        size: Int,
        location: Int
    ): StoriesResponse {
        return dummyStoryResponse
    }

    override suspend fun getMapStories(token: String): StoriesResponse {
        return dummyStoryResponse
    }
}