package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.db.dao.GoodItemDao
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import kotlinx.coroutines.flow.Flow

class GoodItemRepository(private val dao: GoodItemDao) {
    fun observeAll(babyId: Long): Flow<List<GoodItemEntity>> = dao.observeAll(babyId)

    suspend fun add(
        babyId: Long,
        name: String,
        category: String,
        priceYuan: Double,
        rating: Int,
        note: String,
        buyDate: Long
    ): Long =
        dao.insert(
            GoodItemEntity(
                babyId = babyId,
                name = name,
                category = category,
                priceYuan = priceYuan,
                rating = rating,
                note = note,
                buyDate = buyDate,
                createdAt = System.currentTimeMillis()
            )
        )

    suspend fun update(item: GoodItemEntity) = dao.update(item)

    suspend fun delete(item: GoodItemEntity) = dao.delete(item)
}
