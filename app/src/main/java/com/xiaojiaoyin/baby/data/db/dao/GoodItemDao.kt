package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoodItemDao {
    @Query("SELECT * FROM good_item WHERE babyId = :babyId ORDER BY createdAt DESC")
    fun observeAll(babyId: Long): Flow<List<GoodItemEntity>>

    @Query("SELECT * FROM good_item WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): GoodItemEntity?

    @Query("SELECT * FROM good_item")
    suspend fun getAllAll(): List<GoodItemEntity>

    @Insert
    suspend fun insert(item: GoodItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<GoodItemEntity>)

    @Query("DELETE FROM good_item WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)

    @Update
    suspend fun update(item: GoodItemEntity)

    @Delete
    suspend fun delete(item: GoodItemEntity)
}
