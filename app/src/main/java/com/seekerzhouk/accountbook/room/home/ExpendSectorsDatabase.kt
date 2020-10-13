package com.seekerzhouk.accountbook.room.home

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [ExpendSector::class])
abstract class ExpendSectorsDatabase : RoomDatabase() {

    abstract fun getRecordDao(): ExpendSectorDao

    companion object {
        private var instance: ExpendSectorsDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): ExpendSectorsDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                ExpendSectorsDatabase::class.java,
                "expend_sectors"
            ).build().also {
                instance = it
            }
        }
    }
}