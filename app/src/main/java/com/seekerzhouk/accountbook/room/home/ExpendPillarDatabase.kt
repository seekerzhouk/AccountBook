package com.seekerzhouk.accountbook.room.home

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [ExpendPillar::class])
abstract class ExpendPillarDatabase : RoomDatabase() {
    abstract fun getPillarDao(): ExpendPillarDao

    companion object {
        private var instance: ExpendPillarDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): ExpendPillarDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                ExpendPillarDatabase::class.java,
                "expend_pillars"
            ).build().also {
                instance = it
            }
        }
    }
}