package koziol.mooo.com.mkb2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FilterDao {
    @Query("select * from bookmarkedfilter")
    fun getAll(): List<BookmarkedFilter>

    @Insert
    fun insert(filter: BookmarkedFilter)

    @Delete
    fun delte (filter: BookmarkedFilter)
}