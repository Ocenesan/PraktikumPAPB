package com.tifd.projectcomposed.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Tugas(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "matkul")
    val matkul: String = "",
    @ColumnInfo(name = "detail_tugas")
    val detailTugas: String = "",
    @ColumnInfo(name = "selesai")
    val selesai: Boolean = false
) : Parcelable