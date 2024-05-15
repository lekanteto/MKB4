package com.mooo.koziol.mkb2.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("StaticFieldLeak")
object ConfigRepository {

    private lateinit var context: Context
    private val currentUsernameKey = stringPreferencesKey("currentUsername")
    private val currentUserIdKey = intPreferencesKey("currentUserId")
    private val climbCacheUpdated = booleanPreferencesKey("climbCacheUpdated")

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    suspend fun climbCacheIsUpdated(success: Boolean) {
        context.dataStore.edit { settings ->
            settings[climbCacheUpdated] = success
        }
    }

    suspend fun isClimbCacheUpdated(): Boolean? {
        val settings = context.dataStore.data.first()
        return settings[climbCacheUpdated]
    }


    suspend fun saveSession(username: String, userId: Int, sessionToken: String) {
        val userKey = stringPreferencesKey(username)
        context.dataStore.edit { settings ->
            settings[userKey] = sessionToken
            settings[currentUsernameKey] = username
            settings[currentUserIdKey] = userId
        }
        _isLoggedIn.value = true
    }

    suspend fun deleteCurrentSession() {
        val username = getCurrentUsername()
        if (!username.isNullOrEmpty()) {
            val userKey = stringPreferencesKey(username)
            context.dataStore.edit { settings ->
                settings.remove(userKey)
                settings.remove(currentUsernameKey)
                settings.remove(currentUserIdKey)
            }
            _isLoggedIn.value = false
        }
    }

    fun setup(context: Context) {
        this.context = context
        CoroutineScope(Dispatchers.Default).launch {
            _isLoggedIn.value = !getCurrentUsername().isNullOrEmpty()
        }
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
