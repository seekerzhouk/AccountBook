package com.seekerzhouk.accountbook.room.home

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [IncomePillar::class])
abstract class IncomePillarDatabase : RoomDatabase() {
    abstract fun getPillarDao(): IncomePillarDao

    companion object {
        private var instance: IncomePillarDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): IncomePillarDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                IncomePillarDatabase::class.java,
                "income_pillars"
            ).build().also {
                instance = it
            }
        }
    }
}