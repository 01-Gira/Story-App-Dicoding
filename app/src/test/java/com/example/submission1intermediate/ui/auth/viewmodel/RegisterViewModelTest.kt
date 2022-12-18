package com.example.submission1intermediate.ui.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submission1intermediate.DataDummy
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.data.remote.response.RegisterResponse
import com.example.submission1intermediate.data.utils.Result
import com.example.submission1intermediate.getOrWaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(storyRepository)
    }

    @Test
    fun `when getResponseRegister() is called Should Not Null and Return Success`() {
        val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()

        val expectedResponseLogin = MutableLiveData<Result<RegisterResponse>>()
        expectedResponseLogin.value = Result.Success(dummyRegisterResponse)

        Mockito.`when`(storyRepository.getResponseRegister("gira","email123","1234567")).thenReturn(expectedResponseLogin)

        val actualData = registerViewModel.getResponseRegister("gira", "email123", "1234567").getOrWaitValue()


        Mockito.verify(storyRepository).getResponseRegister("gira", "email123", "1234567")
        assertNotNull(expectedResponseLogin)
        assertTrue(actualData is Result.Success)
        assertEquals(expectedResponseLogin.value, actualData)
    }
}