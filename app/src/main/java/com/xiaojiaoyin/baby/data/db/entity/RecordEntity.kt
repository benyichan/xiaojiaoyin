package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecordType {
    FEEDING, CRYING, GROWTH
}

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val type: RecordType,
    val occurredAt: Long,
    val detailJson: String,
    val note: String = "",
    val createdAt: Long
)
