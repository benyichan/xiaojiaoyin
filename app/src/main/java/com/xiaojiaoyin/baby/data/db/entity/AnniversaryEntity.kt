package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "anniversary")
data class AnniversaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 同步唯一标识（跨设备稳定，合并键） */
    val uuid: String = UUID.randomUUID().toString(),
    val babyId: Long,
    /** 纪念日名称，如 百天宴、周岁宴、预产期 */
    val name: String,
    /** 纪念日时间戳（当天 00:00 所在时刻即可） */
    val dateAt: Long,
    /** true = 每年重复（按月/日）；false = 一次性 */
    val repeatYearly: Boolean = true,
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long = 0
)
