package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nickname: String,
    val gender: String,
    val birthDateTime: Long,
    val avatarColorIndex: Int,
    val createdAt: Long
)
