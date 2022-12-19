package com.example.submission1intermediate.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submission1intermediate.CoroutineRule
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.getOrWaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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
class DarkModeViewModelTest {
    @get:Rule
    var coroutinesTestRule = CoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var darkModeViewModel: DarkModeViewModel

    private val darkMode = true

    @Before
    fun setUp() {
        darkModeViewModel = DarkModeViewModel(storyRepository)
    }

    @Test
    fun `when getThemeMode return the right data and not null`() {
        val expectedValue = MutableLiveData<Boolean>()
        expectedValue.value = darkMode

        `when`(storyRepository.getThemeMode()).thenReturn(expectedValue)

        val actualDarkModeState= darkModeViewModel.getThemeMode().getOrWaitValue()

        verify(storyRepository).getThemeMode()
        Assert.assertNotNull(actualDarkModeState)
        Assert.assertEquals(expectedValue.value, actualDarkModeState)
    }

    @Test
    fun `when saveThemeMode function is working`(): Unit = runTest {
        darkModeViewModel.saveThemeMode(darkMode)
        verify(storyRepository).saveThemeMode(darkMode)
    }

}