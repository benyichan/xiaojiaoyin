package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: RecordEntity): Long

    @Query("SELECT * FROM record WHERE babyId = :babyId ORDER BY occurredAt DESC LIMIT :limit")
    fun observeRecent(babyId: Long, limit: Int): Flow<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE babyId = :babyId ORDER BY occurredAt DESC")
    fun observeAll(babyId: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE babyId = :babyId AND type = :type AND occurredAt >= :since ORDER BY occurredAt DESC")
    suspend fun countByTypeSince(babyId: Long, type: RecordType, since: Long): List<RecordEntity>

    @Query("SELECT * FROM record WHERE babyId = :babyId AND type = :type ORDER BY occurredAt DESC LIMIT 1")
    suspend fun lastOfType(babyId: Long, type: RecordType): RecordEntity?

    @Query("SELECT COUNT(*) FROM record")
    suspend fun countAll(): Int
}
