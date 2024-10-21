@file:OptIn(ExperimentalFoundationApi::class)

package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        if (auth.currentUser != null) {
            Log.d("MainActivity", "User is logged in, navigating to ListActivity")
            startActivity(Intent(this, ListActivity::class.java))
        } else {
            Log.d("MainActivity", "User is not logged in, navigating to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Finish MainActivity after starting the appropriate activity
    }
}