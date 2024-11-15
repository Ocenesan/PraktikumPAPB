package com.tifd.projectcomposed.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Tugas::class], version = 2)
abstract class TugasDB : RoomDatabase() {
    abstract fun tugasDao(): TugasDAO

    companion object {
        @Volatile
        private var INSTANCE: TugasDB? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new column for storing photo URI
                db.execSQL("ALTER TABLE tugas ADD COLUMN photo_uri TEXT")
            }
        }

        @JvmStatic
        fun getDatabase(context: Context): TugasDB {
            if (INSTANCE == null) {
                synchronized(TugasDB::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TugasDB::class.java,
                        "DBTugas"
                    )
                        .addMigrations(MIGRATION_1_2) // Apply migration
                        .build()
                }
            }
            return INSTANCE as TugasDB
        }
    }
}