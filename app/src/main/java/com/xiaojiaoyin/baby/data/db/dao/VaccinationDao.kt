package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.VaccinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {
    @Query("SELECT * FROM vaccination_status WHERE babyId = :babyId")
    fun observeByBaby(babyId: Long): Flow<List<VaccinationEntity>>

    @Query("SELECT * FROM vaccination_status WHERE babyId = :babyId AND doseKey = :doseKey")
    suspend fun getByDoseKey(babyId: Long, doseKey: String): VaccinationEntity?

    @Query("SELECT * FROM vaccination_status WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): VaccinationEntity?

    @Query("SELECT * FROM vaccination_status")
    suspend fun getAllAll(): List<VaccinationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<VaccinationEntity>)

    @Query("DELETE FROM vaccination_status WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)

    @Update
    suspend fun update(item: VaccinationEntity)

    @Delete
    suspend fun delete(item: VaccinationEntity)
}
