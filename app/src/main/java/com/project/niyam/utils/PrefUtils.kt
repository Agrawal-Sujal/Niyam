package com.project.niyam.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrefUtils @Inject constructor(@ApplicationContext val context: Context) {

     private val Context.dataStore by preferencesDataStore("local")

    suspend fun saveString(key: String, value: String) {
        Log.d("PrefUtils", key + value)
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String): String? {
        Log.d("PrefUtils", key)
        return context.dataStore.data.map {
            it[stringPreferencesKey(key)]
        }.first()
    }

}