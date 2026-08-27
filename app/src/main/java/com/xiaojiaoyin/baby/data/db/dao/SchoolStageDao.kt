package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolStageDao {
    @Query("SELECT * FROM school_stage WHERE babyId = :babyId ORDER BY startAt ASC")
    fun observeAll(babyId: Long): Flow<List<SchoolStageEntity>>

    @Query("SELECT * FROM school_stage WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): SchoolStageEntity?

    @Query("SELECT * FROM school_stage")
    suspend fun getAllAll(): List<SchoolStageEntity>

    @Insert
    suspend fun insert(stage: SchoolStageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stages: List<SchoolStageEntity>)

    @Query("DELETE FROM school_stage WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)

    @Update
    suspend fun update(stage: SchoolStageEntity)

    @Delete
    suspend fun delete(stage: SchoolStageEntity)
}
