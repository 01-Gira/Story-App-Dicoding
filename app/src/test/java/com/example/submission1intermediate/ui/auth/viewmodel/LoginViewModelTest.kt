package com.example.submission1intermediate.ui.auth.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submission1intermediate.DataDummy
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.data.remote.response.LoginResult
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
class LoginViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(storyRepository)
    }


    @Test
    fun `when getResponseLogin() is called Should Not Null and Return Success`() {
        val dummyResultLogin = DataDummy.generateDummyLoginResponse().loginResult

        val expectedResponseLogin = MutableLiveData<Result<LoginResult>>()
        expectedResponseLogin.value = Result.Success(dummyResultLogin)

        Mockito.`when`(storyRepository.getResponseLogin("email123","1234567")).thenReturn(expectedResponseLogin)

        val actualData = loginViewModel.getResponselogin("email123","1234567").getOrWaitValue()

        Mockito.verify(storyRepository).getResponseLogin("email123", "1234567")
        assertNotNull(expectedResponseLogin)
        assertTrue(actualData is Result.Success)
        assertEquals(expectedResponseLogin.value, actualData)
    }
}