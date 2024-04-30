package com.mooo.koziol.mkb2.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filters")
data class BookmarkedFilter(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "bookmark_name") val name: String,


    @Embedded
    val climbFilter: ClimbFilter,

)
