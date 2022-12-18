package com.example.submission1intermediate.ui.addstory.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.data.remote.response.FileUploadResponse
import com.example.submission1intermediate.data.utils.Result
import com.example.submission1intermediate.getOrWaitValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel



    private val dummyResponse = FileUploadResponse(
        false,
        "success"
    )

    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(storyRepository)
    }

    @Test
    fun `when story() is called Should Not Null and Return Success`() {
        val expectedUploadMessage =  MutableLiveData<Result<FileUploadResponse>>()
        expectedUploadMessage.value = Result.Success(dummyResponse)

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())
        val token = "ini token"

        `when`(storyRepository.upload(
            token,
            imageMultipart,
            description,
        )).thenReturn(expectedUploadMessage)


        val actualResponse = addStoryViewModel.upload(token, imageMultipart, description).getOrWaitValue()

        Mockito.verify(storyRepository).upload(token, imageMultipart, description)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedUploadMessage.value, actualResponse)
    }
}