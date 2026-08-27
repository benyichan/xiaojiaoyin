package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.AnniversaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnniversaryDao {
    @Query("SELECT * FROM anniversary WHERE babyId = :babyId ORDER BY dateAt ASC")
    fun observeByBaby(babyId: Long): Flow<List<AnniversaryEntity>>

    @Query("SELECT * FROM anniversary WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): AnniversaryEntity?

    @Query("SELECT * FROM anniversary WHERE id = :id")
    suspend fun getById(id: Long): AnniversaryEntity?

    @Query("SELECT * FROM anniversary")
    suspend fun getAllAll(): List<AnniversaryEntity>

    @Insert
    suspend fun insert(item: AnniversaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<AnniversaryEntity>)

    @Query("DELETE FROM anniversary WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)

    @Update
    suspend fun update(item: AnniversaryEntity)

    @Delete
    suspend fun delete(item: AnniversaryEntity)
}
