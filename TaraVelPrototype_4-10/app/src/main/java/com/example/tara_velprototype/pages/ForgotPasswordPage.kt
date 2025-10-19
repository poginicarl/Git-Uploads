package com.example.tara_velprototype.pages

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tara_velprototype.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordPage(navController: NavHostController, authViewModel: AuthViewModel) {
    val emailState = remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forgot Password", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Enter Email") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val emailInput = emailState.value.trim()
            if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                showDialog = true
            } else {
                Toast.makeText(context, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Send Approval")
        }
    }

    // This stays inside the Composable function body!
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Forgot Password") },
            text = {
                Text("Send a password reset link to:\n\n${emailState.value.trim()}")
            },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(emailState.value.trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Check your email to reset password.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Registered email not found.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            showDialog = false
                            emailState.value = "" // Clear the text field
                        }
                }) {
                    Text("Submit")
                }
    },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
