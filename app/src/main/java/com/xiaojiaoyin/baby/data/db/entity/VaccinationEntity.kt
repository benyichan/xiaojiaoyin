package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * 疫苗接种状态。日程本身内置在 assets/vaccine_schedule.json（国家免疫规划），
 * 本表只存「已接种」标记，doseKey 对应日程 JSON 的 key（如 hepB-1）。
 */
@Entity(tableName = "vaccination_status")
data class VaccinationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** 同步唯一标识（跨设备稳定，合并键） */
    val uuid: String = UUID.randomUUID().toString(),
    val babyId: Long,
    /** 剂次唯一键，对应 assets/vaccine_schedule.json 的 key */
    val doseKey: String,
    /** 实际接种时间戳；0 = 标记已种但未填具体时间 */
    val vaccinatedAt: Long,
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long = 0
)
