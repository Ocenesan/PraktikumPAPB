package com.tifd.projectcomposed.Navigation

import androidx.compose.ui.graphics.painter.Painter

data class Navigationitem(
    val title: String,
    val icon: Painter,
    val screen: Screen
)
