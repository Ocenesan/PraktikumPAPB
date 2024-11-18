package com.tifd.projectcomposed.local

import androidx.room.Dao
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TugasDAO {
    @Query("SELECT * FROM tugas" )
    fun getAllTugas(): LiveData<List<Tugas>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTugas(tugas: Tugas)

    @Update
    fun update(tugas: Tugas)

    @Query("SELECT * FROM tugas WHERE photo_uri IS NOT NULL")
    fun getTugasWithPhotos(): LiveData<List<Tugas>>

    @Delete
    fun delete(tugas: Tugas)
}