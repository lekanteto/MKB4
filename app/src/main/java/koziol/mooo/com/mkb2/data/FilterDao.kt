package koziol.mooo.com.mkb2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilterDao {
    @Query("select * from filters")
    fun getAll(): List<BookmarkedFilter>

    @Query("select * from filters where id = 1")
    fun getLastActive(): BookmarkedFilter

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filter: BookmarkedFilter)

    @Delete
    fun delete (filter: BookmarkedFilter)
}