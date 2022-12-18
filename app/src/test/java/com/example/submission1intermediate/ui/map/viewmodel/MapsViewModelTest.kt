package com.example.submission1intermediate.ui.map.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.submission1intermediate.DataDummy
import com.example.submission1intermediate.MainDispatcherRule
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.data.remote.response.ListStoryItem
import com.example.submission1intermediate.data.utils.Result
import com.example.submission1intermediate.getOrWaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel


    private val token = "token"

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(storyRepository)
    }

    @Test
    fun `when getStories() should return the right data and not null`() {
        val dummyStories = DataDummy.generateDummyStoryEntity()
        val expectedStories = MutableLiveData<Result<List<ListStoryItem>>>()
        expectedStories.value = Result.Success(dummyStories)

        `when`(storyRepository.getStories(token)).thenReturn(expectedStories)

        val actualStories = mapsViewModel.getStories(token).getOrWaitValue()

        Mockito.verify(storyRepository).getStories(token)

        assertNotNull(actualStories)
        assertTrue(actualStories is Result.Success)
        assertEquals(dummyStories.size, ( actualStories as Result.Success).data.size)
    }
}