package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 同步唯一标识（跨设备稳定，合并键） */
    val uuid: String = UUID.randomUUID().toString(),
    val babyId: Long,
    val title: String,
    val timeAt: Long,
    val remindEnabled: Boolean = false,
    val completed: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long = 0,
    /** 本机已提醒时间戳（不参与同步：各设备应各自响铃一次）；0=未提醒 */
    val remindedAt: Long = 0
)
