package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_custom_field")
data class BabyCustomFieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val fieldKey: String,
    val fieldValue: String,
    val createdAt: Long
)
