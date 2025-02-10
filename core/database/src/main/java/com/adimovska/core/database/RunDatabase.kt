package com.adimovska.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adimovska.core.database.dao.RunDao
import com.adimovska.core.database.entity.RunEntity

@Database(
    entities = [
        RunEntity::class
    ],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {
    abstract val runDao: RunDao
}