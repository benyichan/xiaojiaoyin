package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "good_item")
data class GoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val name: String,
    val category: String = "其他",
    val priceYuan: Double = 0.0,
    val rating: Int,            // 1-5
    val note: String,
    val buyDate: Long = 0,
    val createdAt: Long
)
