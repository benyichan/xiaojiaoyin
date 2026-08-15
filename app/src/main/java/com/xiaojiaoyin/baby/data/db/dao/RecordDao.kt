package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: RecordEntity): Long

    @Query("SELECT * FROM record")
    suspend fun getAllAll(): List<RecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(records: List<RecordEntity>)

    @Query("SELECT * FROM record WHERE babyId = :babyId ORDER BY occurredAt DESC LIMIT :limit")
    fun observeRecent(babyId: Long, limit: Int): Flow<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE babyId = :babyId ORDER BY occurredAt DESC")
    fun observeAll(babyId: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE babyId = :babyId AND type = :type ORDER BY occurredAt DESC")
    fun observeByType(babyId: Long, type: RecordType): Flow<List<RecordEntity>>

    @Query("SELECT * FROM record WHERE babyId = :babyId AND type = :type AND occurredAt >= :since ORDER BY occurredAt DESC")
    suspend fun countByTypeSince(babyId: Long, type: RecordType, since: Long): List<RecordEntity>

    @Query("SELECT * FROM record WHERE babyId = :babyId AND type = :type ORDER BY occurredAt DESC LIMIT 1")
    suspend fun lastOfType(babyId: Long, type: RecordType): RecordEntity?

    @Query("SELECT COUNT(*) FROM record")
    suspend fun countAll(): Int

    @Delete
    suspend fun delete(record: RecordEntity)

    @Update
    suspend fun update(record: RecordEntity)
}
