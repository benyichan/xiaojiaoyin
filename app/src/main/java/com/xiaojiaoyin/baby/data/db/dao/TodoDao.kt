package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE babyId = :babyId ORDER BY timeAt ASC")
    fun observeAll(babyId: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE remindEnabled = 1 AND completed = 0 AND timeAt > :now")
    suspend fun pendingReminders(now: Long): List<TodoEntity>

    @Insert
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
