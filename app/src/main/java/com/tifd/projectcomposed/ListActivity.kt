package com.tifd.projectcomposed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tifd.projectcomposed.screen.MatkulScreen
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class ListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectComposeDTheme {
                MatkulScreen()
            }
        }
    }
}