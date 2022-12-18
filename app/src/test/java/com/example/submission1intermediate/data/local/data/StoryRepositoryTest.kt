package com.example.submission1intermediate.data.local.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submission1intermediate.CoroutineRule
import com.example.submission1intermediate.DataDummy
import com.example.submission1intermediate.MainDispatcherRule
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.data.remote.response.ListStoryItem
import com.example.submission1intermediate.data.remote.retrofit.ApiService
import com.example.submission1intermediate.data.remote.retrofit.FakeApiService
import com.example.submission1intermediate.getOrWaitValue
import com.example.submission1intermediate.ui.adapter.ListStoryAdapter
import com.example.submission1intermediate.ui.home.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()


    @Mock
    private lateinit var userPreference: UserPreference


    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var apiService: ApiService
    private var dummmyName = "user"
    private var dummyEmail = "user@email.com"
    private var dummyPass = "userPassword"
    private val dummyToken = "token"

    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        apiService = FakeApiService()
        storyRepository = Mockito.mock(StoryRepository::class.java)
    }


    @Test
    fun `when Get PagingStory Should Not Null and Return Success`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryEntity()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        `when`(storyRepository.getStory(dummyToken)).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(storyRepository)
        val actualStory: PagingData<ListStoryItem> = mainViewModel.getStory(dummyToken).getOrWaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories, differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when getMapStories() is called Should Not Null`() = runTest {
        val expectedStory = DataDummy.generateDummyStoriesResponse()
        val actualStory = apiService.getMapStories(dummyToken)
        assertNotNull(actualStory)
        assertEquals(expectedStory.listStory.size, actualStory.listStory.size)
    }

    @Test
    fun `when getResponselogin() is called Should  Not Null`() = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()
        val actualResponse =
            apiService.login(dummyEmail, dummyPass)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when getResponseRegister() is called Should  Not Null`() = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()
        val actualResponse =
            apiService.register(dummmyName, dummyEmail, dummyPass)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }
    
    @Test
    fun `when postStory() is called Should Not Null`() = runTest {
        val expectedResponse = DataDummy.generateDummyFileUploadResponse()

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())

        val actualResponse = apiService.uploadImage(dummyToken, imageMultipart, description)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }




    class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
