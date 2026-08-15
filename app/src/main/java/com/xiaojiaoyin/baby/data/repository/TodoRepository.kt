package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.db.dao.TodoDao
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDao) {
    fun observeAll(babyId: Long): Flow<List<TodoEntity>> = dao.observeAll(babyId)

    suspend fun pendingReminders(now: Long): List<TodoEntity> = dao.pendingReminders(now)

    suspend fun add(babyId: Long, title: String, timeAt: Long, remindEnabled: Boolean): Long =
        dao.insert(
            TodoEntity(
                babyId = babyId,
                title = title,
                timeAt = timeAt,
                remindEnabled = remindEnabled,
                createdAt = System.currentTimeMillis()
            )
        )

    suspend fun update(todo: TodoEntity) = dao.update(todo)

    suspend fun delete(todo: TodoEntity) = dao.delete(todo)
}
