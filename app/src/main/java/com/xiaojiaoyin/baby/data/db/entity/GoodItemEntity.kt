package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "good_item")
data class GoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val name: String,
    val rating: Int,            // 1-5
    val note: String,
    val createdAt: Long
)
