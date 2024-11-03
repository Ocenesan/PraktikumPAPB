package com.tifd.projectcomposed.local

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val nTugasDAO: TugasDAO
    private val executorService: ExecutorService = Executors.newSingleThreadScheduledExecutor()
    init {
        val db = TugasDB.getDatabase(application)
        nTugasDAO = db.tugasDao()
    }
    fun getAllTugas(): LiveData<List<Tugas>> = nTugasDAO.getAllTugas()
    fun insert(tugas: Tugas){
        executorService.execute { nTugasDAO.insertTugas(tugas)}
    }
    fun update(tugas: Tugas) {
        executorService.execute { nTugasDAO.update(tugas) } // You'll need an update query in TugasDAO
    }
}