package com.tifd.projectcomposed.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberImagePainter
import com.tifd.projectcomposed.network.GitHubUser
import com.tifd.projectcomposed.network.RetrofitClient

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var user by remember { mutableStateOf<GitHubUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch data using coroutine in LaunchedEffect
    LaunchedEffect(Unit) {
        Log.d("ProfileScreen", "Fetching data...") // Log before the request
        try {
            user = RetrofitClient.apiService.getUser("Ocenesan")
            Log.d("ProfileScreen", "Data fetched: $user") // Log the fetched user
            isLoading = false
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error fetching data: ${e.message}") // Log the error
            errorMessage = "Failed to fetch data: ${e.message}"
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = errorMessage ?: "Unknown error")
        }
    } else {
        user?.let { githubUser ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = rememberImagePainter(data = githubUser.avatar_url),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape) // Make the image circular
                )

                Spacer(Modifier.height(16.dp))

                Text(text = githubUser.name ?: "No Name", style = MaterialTheme.typography.headlineMedium)
                Text(text = "@${githubUser.login}", style = MaterialTheme.typography.bodyLarge)

                Spacer(Modifier.height(16.dp))

                Row {
                    Text(text = "Followers: ${githubUser.followers}")
                    Spacer(Modifier.width(16.dp))
                    Text(text = "Following: ${githubUser.following}")
                }

                // Open GitHub profile in browser when clicked
                Text(
                    text = "View on GitHub",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/${githubUser.login}"))
                            startActivity(context, intent, null)
                        }
                )
            }
        }
    }
}