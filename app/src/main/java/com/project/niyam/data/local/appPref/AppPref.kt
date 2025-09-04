package com.project.niyam.data.local.appPref

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appDataStore by preferencesDataStore(name = "app_pref")

class AppPref(private val context: Context) {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val TOKEN = stringPreferencesKey("token")

        private val EMAIL = stringPreferencesKey("email")
    }

    val userId: Flow<String?> = context.appDataStore.data.map { it[USER_ID] }
    val username: Flow<String?> = context.appDataStore.data.map { it[USERNAME] }
    val token: Flow<String?> = context.appDataStore.data.map { it[TOKEN] }

    val email : Flow<String?> = context.appDataStore.data.map { it[EMAIL] }

    suspend fun saveUser(userId: String, username: String, token: String,email : String? = null) {
        context.appDataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USERNAME] = username
            prefs[TOKEN] = token
            prefs[EMAIL] = token
        }
    }

    suspend fun clearUser() {
        context.appDataStore.edit { it.clear() }
    }
}
