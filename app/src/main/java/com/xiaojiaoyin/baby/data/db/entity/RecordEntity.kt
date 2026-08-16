package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RecordType {
    FEEDING, CRYING, GROWTH, PHOTO, NODE, MEDICAL
}

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val type: RecordType,
    val occurredAt: Long,
    val detailJson: String,
    val note: String = "",
    /** 照片标签，逗号分隔，如 "关键时刻,满月" */
    val tags: String = "",
    /** 关联的成长节点 id（节点配图生成的相册照片） */
    val parentId: Long? = null,
    val createdAt: Long,
    val updatedAt: Long = 0
)

/** 节点配图自动打入的标签 */
const val TAG_KEY_MOMENT = "关键时刻"

fun RecordEntity.tagsList(): List<String> =
    tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

fun RecordEntity.withTags(tagList: List<String>): RecordEntity =
    copy(
        tags = tagList
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .joinToString(",")
    )
