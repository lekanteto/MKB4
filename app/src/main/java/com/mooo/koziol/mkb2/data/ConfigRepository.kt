package com.mooo.koziol.mkb2.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("StaticFieldLeak")
object ConfigRepository {

    private lateinit var context: Context
    private val currentUsernameKey = stringPreferencesKey("currentUsername")
    private val currentUserIdKey = intPreferencesKey("currentUserId")

    suspend fun saveSession(username: String, userId: Int, sessionToken: String) {
        val userKey = stringPreferencesKey(username)
        context.dataStore.edit { settings ->
            settings[userKey] = sessionToken
            settings[currentUsernameKey] = username
            settings[currentUserIdKey] = userId
        }
    }

    fun setup(context: Context) {
        this.context = context
    }

    suspend fun getSessionTokenForUser(username: String): String? {
        val userKey = stringPreferencesKey(username)
        val settings = context.dataStore.data.first()
        return settings[userKey]
    }

    suspend fun getCurrentUsername(): String? {
        val settings = context.dataStore.data.first()
        return settings[currentUsernameKey]
    }

    suspend fun getCurrentUserId(): Int? {
        val settings = context.dataStore.data.first()
        return settings[currentUserIdKey]
    }

}
