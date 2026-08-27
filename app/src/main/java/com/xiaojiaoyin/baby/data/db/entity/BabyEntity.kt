package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "baby")
data class BabyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 同步唯一标识（跨设备稳定，合并键；自增 id 仅本机有效） */
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val nickname: String,
    val gender: String,
    val birthDateTime: Long,
    val avatarColorIndex: Int,
    /** 头像照片相对路径（filesDir 下），空 = 无照片用名字首字 */
    val avatarPath: String = "",
    val createdAt: Long,
    val updatedAt: Long = 0
)
