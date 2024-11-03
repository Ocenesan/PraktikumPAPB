package com.tifd.projectcomposed.viewmodel

import android.app.Application // Import Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tifd.projectcomposed.local.TugasRepository

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {  // Use Application
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // Create TugasRepository here
            val tugasRepository = TugasRepository(application)
            return MainViewModel(tugasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}