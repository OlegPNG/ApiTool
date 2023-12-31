package com.beck.apitool.Databases_Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Session::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}