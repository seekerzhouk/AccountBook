package com.seekerzhouk.accountbook.room.details

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Record::class])
abstract class RecordsDatabase : RoomDatabase() {

    abstract fun getRecordDao(): RecordDao

    companion object {
        private var instance: RecordsDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): RecordsDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                RecordsDatabase::class.java,
                "detail_records"
            ).build().also {
                instance = it
            }
        }
    }
}