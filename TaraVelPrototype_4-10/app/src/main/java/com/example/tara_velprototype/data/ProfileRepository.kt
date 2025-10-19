package com.example.tara_velprototype.data

class ProfileRepository(private val dataStoreManager: DataStoreManager) {
    val userName = dataStoreManager.userName
    val userEmail = dataStoreManager.userEmail

    suspend fun saveProfile(name: String, email: String) {
        dataStoreManager.saveProfile(name, email)
    }
}
