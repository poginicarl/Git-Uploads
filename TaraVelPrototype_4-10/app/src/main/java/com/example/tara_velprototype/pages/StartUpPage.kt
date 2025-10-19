package com.example.tara_velprototype.pages

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tara_velprototype.AuthViewModel
import com.example.tara_velprototype.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
fun StartUpPage(navController: NavHostController, authViewModel: AuthViewModel) {
    val context = LocalContext.current

    var locationPermissionRequested by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (!fineGranted || !coarseGranted) {
                (context as? Activity)?.finishAffinity()
            }
        }
    )

    // PERMISSIONS (WORKING)

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted || !coarseGranted) {
            if (!locationPermissionRequested) {
                locationPermissionRequested = true
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 🔹 Background Image
        Image(
            painter = painterResource(id = R.drawable.startuppage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 🔹 Foreground Buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Login Button (Rectangle shape)
            // 🔹 Login Button
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF003366),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp), // 👈 smoother corners
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 11.dp)
            ) {
                Text(text = "Login", fontSize = 20.sp)
            }

// 🔹 Register Button
            Button(
                onClick = { navController.navigate("signup") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp), // 👈 smoother corners
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            ) {
                Text(text = "Register? Sign Up", fontSize = 20.sp)
            }
        }
    }
}