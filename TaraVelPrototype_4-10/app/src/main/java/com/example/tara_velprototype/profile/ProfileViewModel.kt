package com.example.tara_velprototype.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tara_velprototype.data.DataStoreManager
import com.example.tara_velprototype.data.ProfileRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProfileRepository

    val userName: StateFlow<String>
    val userEmail: StateFlow<String>

    init {
        val dataStoreManager = DataStoreManager(application)
        repository = ProfileRepository(dataStoreManager)

        userName = repository.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
        userEmail = repository.userEmail.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    }

    fun saveProfile(name: String, email: String) {
        viewModelScope.launch {
            repository.saveProfile(name, email)
        }
    }
}
