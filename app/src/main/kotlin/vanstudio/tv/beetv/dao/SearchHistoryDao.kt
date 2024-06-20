package vanstudio.tv.beetv.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import vanstudio.tv.beetv.entity.db.SearchHistoryDB

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history")
    suspend fun getAll(): List<SearchHistoryDB>

    @Query("SELECT * FROM search_history ORDER BY search_date DESC LIMIT :count")
    suspend fun getHistories(count: Int): List<SearchHistoryDB>

    @Query("SELECT * FROM search_history WHERE keyword = :keyword LIMIT 1")
    suspend fun findHistory(keyword: String): SearchHistoryDB?

    @Insert
    suspend fun insert(vararg searchHistoryDB: SearchHistoryDB)

    @Delete
    suspend fun delete(vararg searchHistoryDB: SearchHistoryDB)

    @Update
    suspend fun update(searchHistoryDB: SearchHistoryDB)
}