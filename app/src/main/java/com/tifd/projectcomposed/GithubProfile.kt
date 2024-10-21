package com.tifd.projectcomposed

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class GithubProfile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                GithubProfileScreen()
            }
        }
    }
}

@Composable
fun GithubProfileScreen() {
    val context = LocalContext.current

    // Replace with your actual GitHub profile information
    val profilePicture = R.drawable.kuromi
    val username = "Ocenesan"
    val name = "Emilia"
    val followers = 1234
    val following = 567

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.kuromi),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape) // Make the image circular
        )

        Text(text = name, style = MaterialTheme.typography.headlineMedium)
        Text(text = "@$username", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(16.dp))

        Row {
            Text(text = "Followers: $followers")
            Spacer(Modifier.width(16.dp))
            Text(text = "Following: $following")
        }

        // Open GitHub profile in browser when clicked
        Text(
            text = "View on GitHub",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/$username"))
                    startActivity(context, intent, null)
                }
        )

    }
}