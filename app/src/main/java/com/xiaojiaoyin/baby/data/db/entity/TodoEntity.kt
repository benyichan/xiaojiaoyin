package com.xiaojiaoyin.baby.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val babyId: Long,
    val title: String,
    val timeAt: Long,
    val remindEnabled: Boolean = false,
    val completed: Boolean = false,
    val createdAt: Long
)
