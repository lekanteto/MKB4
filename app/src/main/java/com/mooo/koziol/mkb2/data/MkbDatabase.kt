package com.mooo.koziol.mkb2.data

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [BookmarkedFilter::class], version = 1)
abstract class MkbDatabase: RoomDatabase() {
    abstract fun filterDao(): FilterDao
}