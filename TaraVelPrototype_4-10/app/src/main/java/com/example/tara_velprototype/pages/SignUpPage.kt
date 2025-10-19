package com.example.tara_velprototype.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tara_velprototype.AuthViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Watch for signup success and send verification email
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthViewModel.AuthState.SignUpSuccess -> {
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    it.sendEmailVerification().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Account created! Please verify your email before logging in.",
                                Toast.LENGTH_LONG
                            ).show()

                            // Force logout until email is verified
                            FirebaseAuth.getInstance().signOut()

                            // Go back to login
                            navController.navigate("login") {
                                popUpTo("signup") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            is AuthViewModel.AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState.value as AuthViewModel.AuthState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign Up", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        authViewModel.signup(email, password, fullName) // pass fullname
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = authState.value != AuthViewModel.AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
    }
}
