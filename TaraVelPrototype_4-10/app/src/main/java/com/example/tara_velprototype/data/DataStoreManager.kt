package com.example.tara_velprototype.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("user_prefs")

    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    }

    private fun userNameKey() = stringPreferencesKey("${getUserId()}_user_name")
    private fun userEmailKey() = stringPreferencesKey("${getUserId()}_user_email")

    val userName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[userNameKey()] ?: "" }

    val userEmail: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[userEmailKey()] ?: "" }

    suspend fun saveProfile(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[userNameKey()] = name
            prefs[userEmailKey()] = email
        }
    }
}
