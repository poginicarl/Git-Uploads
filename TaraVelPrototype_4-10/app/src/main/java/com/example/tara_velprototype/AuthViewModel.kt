package com.example.tara_velprototype

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    // Expose the current user email
    val currentUserEmail: String?
        get() = auth.currentUser?.email

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    // UPDATED to accept name and save to Firestore
    fun signup(email: String, password: String, name: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedName = name.trim()

        if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty() || trimmedName.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Firestore user document
                        val userData = hashMapOf(
                            "createdAt" to FieldValue.serverTimestamp(), // server time
                            "email" to trimmedEmail,
                            "name" to trimmedName,
                            "role" to "user",
                            "status" to "active"
                        )

                        firestore.collection("users")
                            .document(it.uid) // use Firebase UID
                            .set(userData)
                            .addOnSuccessListener {
                                _authState.value = AuthState.SignUpSuccess
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error("Failed to save user: ${e.message}")
                            }
                    } ?: run {
                        _authState.value = AuthState.Error("User creation failed")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun updateEmailAndPassword(
        newEmail: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                if (emailTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { passTask ->
                        if (passTask.isSuccessful) {
                            callback(true, "Credentials updated successfully.")
                        } else {
                            callback(false, passTask.exception?.message ?: "Failed to update password.")
                        }
                    }
                } else {
                    callback(false, emailTask.exception?.message ?: "Failed to update email.")
                }
            }
        } else {
            callback(false, "No user logged in.")
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object SignUpSuccess : AuthState()
        class Error(val message: String) : AuthState()
    }
}
