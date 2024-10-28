package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tifd.projectcomposed.screen.LoginScreen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContent {
            ProjectComposeDTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) { // Use Surface for background
                    LoginScreen(navController = navController, onLoginSuccess = {
                        // Navigate to MainActivity after successful login
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Finish LoginActivity to prevent going back
                    })
                }
            }
        }
    }
}