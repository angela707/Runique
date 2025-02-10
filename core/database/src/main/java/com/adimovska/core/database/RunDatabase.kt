package com.adimovska.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adimovska.core.database.dao.RunDao
import com.adimovska.core.database.dao.RunPendingSyncDao
import com.adimovska.core.database.entity.DeletedRunSyncEntity
import com.adimovska.core.database.entity.RunEntity
import com.adimovska.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {
    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
}