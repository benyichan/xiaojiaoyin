package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "school_stage")
data class SchoolStageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 同步唯一标识（跨设备稳定，合并键） */
    val uuid: String = UUID.randomUUID().toString(),
    val babyId: Long,
    val stageType: String,      // 幼儿园/小学/初中/高中/大学/其他
    val className: String,      // 班级/年级
    val schoolName: String,
    val teacher: String,
    val startAt: String,        // "2029-09"
    val endAt: String,
    val studentNo: String,
    val costYuan: Double = 0.0,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long = 0
)
