package com.mooo.koziol.mkb2.data

import com.mooo.koziol.mkb2.data.room.FilterDao

object BookmarksRepo {

    private lateinit var filterDao: FilterDao
    fun setup(bookmarkDao: FilterDao) {
        filterDao = bookmarkDao
    }




}