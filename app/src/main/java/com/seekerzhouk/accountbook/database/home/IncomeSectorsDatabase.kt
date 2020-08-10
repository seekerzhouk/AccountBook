package com.seekerzhouk.accountbook.database.home

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [IncomeSector::class])
abstract class IncomeSectorsDatabase : RoomDatabase() {

    abstract fun getRecordDao(): IncomeSectorDao

    companion object {
        private var instance: IncomeSectorsDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): IncomeSectorsDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                IncomeSectorsDatabase::class.java,
                "income_sectors"
            ).build().also {
                instance = it
            }
        }
    }
}