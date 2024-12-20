package com.tifd.projectcomposed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tifd.projectcomposed.screen.ProfileScreen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class GithubProfile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                ProfileScreen()
            }
        }
    }
}