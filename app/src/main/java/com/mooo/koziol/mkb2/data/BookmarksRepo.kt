package com.mooo.koziol.mkb2.data

object BookmarksRepo {

    private lateinit var filterDao: FilterDao
    fun setup(bookmarkDao: FilterDao) {
        filterDao = bookmarkDao
    }




}