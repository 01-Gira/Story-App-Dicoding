package com.example.submission1intermediate.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submission1intermediate.data.local.data.StoryRepository
import com.example.submission1intermediate.data.local.database.StoryDatabase
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.data.remote.retrofit.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService, userPreference)
    }
}