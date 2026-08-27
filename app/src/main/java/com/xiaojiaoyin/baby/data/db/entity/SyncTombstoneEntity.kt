package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity

/**
 * 同步删除墓碑：记录某设备删除过的 (表, uuid)，随变更集传输，
 * 防止已删除记录在对端合并时复活。
 * 墓碑只增不减（数据量 = 累计删除数，宝宝 App 量级可接受）。
 */
@Entity(tableName = "sync_tombstone", primaryKeys = ["tableName", "uuid"])
data class SyncTombstoneEntity(
    val tableName: String,   // baby/record/todo/school/good/custom_field
    val uuid: String,
    val deletedAt: Long
)