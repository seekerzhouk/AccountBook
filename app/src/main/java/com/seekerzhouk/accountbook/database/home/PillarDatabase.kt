package com.seekerzhouk.accountbook.database.home

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Pillar::class])
abstract class PillarDatabase : RoomDatabase() {
    abstract fun getPillarDao(): PillarDao

    companion object {
        private var instance: PillarDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): PillarDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                PillarDatabase::class.java,
                "home_pillars"
            ).build().also {
                instance = it
            }
        }
    }
}