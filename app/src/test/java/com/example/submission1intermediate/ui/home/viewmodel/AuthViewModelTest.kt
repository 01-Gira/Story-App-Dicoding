package com.example.submission1intermediate.ui.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submission1intermediate.CoroutineRule
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.getOrWaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner



@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var authViewModel: AuthViewModel

    private val dummyToken = "token"
    private val loginState = true

    @Before
    fun setUp() {
        authViewModel = AuthViewModel(storyRepository)
    }

    @Test
    fun `when isLoginState return the right data and not null`():Unit = runTest   {
        val expectedLoginState = MutableLiveData<Boolean>()
        expectedLoginState.value = loginState

        `when`(storyRepository.isLogin()).thenReturn(expectedLoginState)

        val actualLoginState = authViewModel.isLogin().getOrWaitValue()

        verify(storyRepository).isLogin()
        assertNotNull(actualLoginState)
        assertEquals(expectedLoginState.value, actualLoginState)
    }
    @Test
    fun `verify LoginState function is working`(): Unit = runTest {
        authViewModel.loginState()
        verify(storyRepository).loginState()
    }

    @Test
    fun `saveToken and name successfully`(): Unit = runTest {

        authViewModel.saveToken("123", "gira")
        verify(storyRepository).saveToken("123","gira")
    }

    @Test
    fun `logout successfully`(): Unit = runTest {
        authViewModel.logout()
        verify(storyRepository).logout()
    }


    @Test
    fun `when getToken return the right data and not null`() {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken

        `when`(storyRepository.getToken()).thenReturn(expectedToken)

        val actualToken = authViewModel.getToken().getOrWaitValue()

        verify(storyRepository).getToken()
        assertNotNull(actualToken)
        assertEquals(expectedToken.value, actualToken)
    }

}