package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity

@Dao
interface SyncTombstoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tombstone: SyncTombstoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tombstones: List<SyncTombstoneEntity>)

    @Query("SELECT * FROM sync_tombstone")
    suspend fun getAllAll(): List<SyncTombstoneEntity>

    @Query("DELETE FROM sync_tombstone WHERE tableName = :tableName AND uuid = :uuid")
    suspend fun delete(tableName: String, uuid: String)

    @Query("DELETE FROM sync_tombstone")
    suspend fun clearAll()
}