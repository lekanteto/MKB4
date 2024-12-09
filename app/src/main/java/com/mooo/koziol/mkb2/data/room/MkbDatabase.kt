package com.mooo.koziol.mkb2.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookmarkedFilter::class], version = 3)
abstract class MkbDatabase: RoomDatabase() {
    abstract fun filterDao(): FilterDao
}