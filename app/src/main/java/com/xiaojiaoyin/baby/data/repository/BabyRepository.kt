package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.db.dao.BabyDao
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import kotlinx.coroutines.flow.Flow

class BabyRepository(private val dao: BabyDao) {
    fun observeAll(): Flow<List<BabyEntity>> = dao.observeAll()

    suspend fun getAll(): List<BabyEntity> = dao.getAll()

    suspend fun getById(id: Long): BabyEntity? = dao.getById(id)

    suspend fun add(baby: BabyEntity): Long = dao.insert(baby)

    suspend fun update(baby: BabyEntity) = dao.update(baby)

    suspend fun delete(baby: BabyEntity) = dao.delete(baby)
}
