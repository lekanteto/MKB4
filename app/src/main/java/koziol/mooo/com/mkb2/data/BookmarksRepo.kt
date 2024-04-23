package koziol.mooo.com.mkb2.data

object BookmarksRepo {

    private lateinit var filterDao: FilterDao
    fun setup(bookmarkDao: FilterDao) {
        filterDao = bookmarkDao
    }
}