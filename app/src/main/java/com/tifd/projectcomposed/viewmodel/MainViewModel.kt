package com.tifd.projectcomposed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tifd.projectcomposed.local.Tugas
import com.tifd.projectcomposed.local.TugasRepository
import kotlinx.coroutines.launch

class MainViewModel(private val tugasRepository: TugasRepository) : ViewModel() {
    private val _tugasList = tugasRepository.getAllTugas()
    val tugasList: LiveData<List<Tugas>> get() = _tugasList

    private val _tugasWithPhotos = MutableLiveData<List<Tugas>>()
    val tugasWithPhotos: LiveData<List<Tugas>> get() = _tugasWithPhotos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchAllTugas()
        fetchTugasWithPhotos()
    }

    private fun fetchAllTugas() {
        viewModelScope.launch {
            try {
                tugasRepository.getAllTugas()
            } catch (e: Exception) {
                _error.postValue("Failed to fetch Tugas: ${e.message}")
            }
        }
    }

    fun addTugas(matkul: String, detailTugas: String, photoUri: String? = null) {
        if (matkul.isBlank() || detailTugas.isBlank()) {
            _error.value = "Matkul and Detail Tugas cannot be empty"
            return
        }

        val newTugas = Tugas(
            matkul = matkul,
            detailTugas = detailTugas,
            selesai = false,
            photoUri = photoUri // Optional photo URI
        )

        viewModelScope.launch {
            try {
                tugasRepository.insert(newTugas)
                _error.value = null
            } catch (e: Exception) {
                _error.postValue("Failed to add Tugas: ${e.message}")
            }
        }
    }

    fun updateTugas(tugas: Tugas) {
        viewModelScope.launch {
            try {
                tugasRepository.update(tugas)
            } catch (e: Exception) {
                _error.postValue("Failed to update Tugas: ${e.message}")
            }
        }
    }
    private fun fetchTugasWithPhotos() {
        viewModelScope.launch {
            try {
                _tugasWithPhotos.value = tugasRepository.getTugasWithPhotos().value
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}