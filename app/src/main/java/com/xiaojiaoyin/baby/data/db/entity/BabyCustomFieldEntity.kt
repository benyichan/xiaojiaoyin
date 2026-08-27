package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "baby_custom_field")
data class BabyCustomFieldEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 同步唯一标识（跨设备稳定，合并键） */
    val uuid: String = UUID.randomUUID().toString(),
    val babyId: Long,
    val fieldKey: String,
    val fieldValue: String,
    val createdAt: Long,
    val updatedAt: Long = 0
)
