package com.project.storyapp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun getToken(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                token = preferences[TOKEN_KEY].orEmpty()
            )
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(dataStore).also {
                    INSTANCE = it
                }
            }
    }
}

data class User(
    val token: String
)