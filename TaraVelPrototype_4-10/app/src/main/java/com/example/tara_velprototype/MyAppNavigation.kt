package com.example.tara_velprototype

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tara_velprototype.pages.ForgotPasswordPage
import com.example.tara_velprototype.pages.LoginPage
import com.example.tara_velprototype.pages.SignUpPage
import com.example.tara_velprototype.pages.UpdatePage
import com.example.tara_velprototype.pages.StartUpPage
import com.example.tara_velprototype.pages.UserDashBoardPage
import com.example.tara_velprototype.profile.UserUploadID // ✅ make sure this import matches your package

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "startup") {
        composable("startup") {
            StartUpPage(navController, authViewModel)
        }
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("forgotpassword") {
            ForgotPasswordPage(navController, authViewModel)
        }
        composable("update") {
            UpdatePage(navController, authViewModel)
        }
        composable("userdashboard") {
            UserDashBoardPage(modifier, navController, authViewModel)
        }
        composable("uploadid") {
            UserUploadID(navController)
        }
    }
}
