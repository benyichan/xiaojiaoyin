package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.BabyCustomFieldEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyCustomFieldDao {
    @Query("SELECT * FROM baby_custom_field WHERE babyId = :babyId ORDER BY createdAt ASC")
    fun observeAll(babyId: Long): Flow<List<BabyCustomFieldEntity>>

    @Query("SELECT * FROM baby_custom_field WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): BabyCustomFieldEntity?

    @Query("SELECT * FROM baby_custom_field")
    suspend fun getAllAll(): List<BabyCustomFieldEntity>

    @Insert
    suspend fun insert(field: BabyCustomFieldEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(fields: List<BabyCustomFieldEntity>)

    @Query("DELETE FROM baby_custom_field WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)

    @Update
    suspend fun update(field: BabyCustomFieldEntity)

    @Delete
    suspend fun delete(field: BabyCustomFieldEntity)
}
