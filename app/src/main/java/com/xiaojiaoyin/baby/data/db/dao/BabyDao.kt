package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Query("SELECT * FROM baby ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<BabyEntity>>

    @Query("SELECT * FROM baby ORDER BY createdAt ASC")
    suspend fun getAll(): List<BabyEntity>

    @Query("SELECT * FROM baby WHERE id = :id")
    suspend fun getById(id: Long): BabyEntity?

    @Insert
    suspend fun insert(baby: BabyEntity): Long

    @Update
    suspend fun update(baby: BabyEntity)

    @Delete
    suspend fun delete(baby: BabyEntity)
}
