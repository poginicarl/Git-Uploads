package com.example.tara_velprototype.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tara_velprototype.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.observeAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ✅ Track if login button was clicked
    var loginRequested by remember { mutableStateOf(false) }

    LaunchedEffect(authState, loginRequested) {
        if (loginRequested) {
            when (authState) {
                is AuthViewModel.AuthState.Authenticated -> {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.isEmailVerified) {
                        // Check Firestore user document for block status
                        FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val blockedAt = document.get("blocked_at")
                                    val blockedReason = document.getString("blocked_reason")

                                    if (blockedAt != null && !blockedReason.isNullOrEmpty()) {
                                        // User is blocked
                                        Toast.makeText(
                                            context,
                                            "Login blocked: $blockedReason",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Clear credentials & sign out
                                        email = ""
                                        password = ""
                                        FirebaseAuth.getInstance().signOut()
                                    } else {
                                        // ✅ Not blocked → proceed to dashboard
                                        navController.navigate("userdashboard") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "User record not found in database.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    FirebaseAuth.getInstance().signOut()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Failed to check account status.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                FirebaseAuth.getInstance().signOut()
                            }
                    } else {
                        // ❌ Email not verified
                        Toast.makeText(
                            context,
                            "Please verify your email first",
                            Toast.LENGTH_LONG
                        ).show()
                        email = ""
                        password = ""
                        FirebaseAuth.getInstance().signOut()
                    }
                }

                is AuthViewModel.AuthState.Error -> {
                    Toast.makeText(
                        context,
                        (authState as AuthViewModel.AuthState.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login", fontSize = 32.sp)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    loginRequested = true
                    authViewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("forgotpassword") }) {
                Text("Forgot Password?")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}
