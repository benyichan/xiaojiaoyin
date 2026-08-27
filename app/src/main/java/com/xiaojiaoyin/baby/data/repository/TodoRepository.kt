package com.xiaojiaoyin.baby.data.repository

import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.dao.TodoDao
import com.xiaojiaoyin.baby.data.db.entity.SyncTombstoneEntity
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDao) {
    fun observeAll(babyId: Long): Flow<List<TodoEntity>> = dao.observeAll(babyId)

    /** 全部待提醒项（含已过期的，用于启动重排补发；过去时间闹钟会立即触发） */
    suspend fun pendingReminders(): List<TodoEntity> = dao.pendingReminders()

    /** 标记已提醒（本机状态，不刷新 updatedAt、不参与同步） */
    suspend fun markReminded(todo: TodoEntity) =
        dao.update(todo.copy(remindedAt = System.currentTimeMillis()))

    suspend fun add(babyId: Long, title: String, timeAt: Long, remindEnabled: Boolean): Long =
        dao.insert(
            TodoEntity(
                babyId = babyId,
                title = title,
                timeAt = timeAt,
                remindEnabled = remindEnabled,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )

    suspend fun update(todo: TodoEntity) =
        dao.update(todo.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(todo: TodoEntity) {
        AppGraph.database.syncTombstoneDao()
            .upsert(SyncTombstoneEntity("todo", todo.uuid, System.currentTimeMillis()))
        dao.delete(todo)
    }
}
