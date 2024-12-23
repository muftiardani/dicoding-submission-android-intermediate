package com.project.storyapp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Class untuk mengelola preferensi user menggunakan DataStore
 * Menyimpan dan mengelola token autentikasi
 */
class UserPreference private constructor(
    private val dataStore: DataStore<Preferences>
) {
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

    /**
     * Mengambil data user (token) sebagai Flow
     * @return Flow<User> data user dalam bentuk Flow
     */
    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                token = preferences[TOKEN_KEY].orEmpty()
            )
        }
    }

    /**
     * Menyimpan token autentikasi
     * @param token String token yang akan disimpan
     */
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    /**
     * Menghapus token autentikasi
     */
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}

/**
 * Data class yang merepresentasikan data user
 * @property token String token autentikasi user
 */
data class User(
    val token: String
)