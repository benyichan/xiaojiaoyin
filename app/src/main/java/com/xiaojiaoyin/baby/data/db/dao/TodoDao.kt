package com.xiaojiaoyin.baby.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE babyId = :babyId ORDER BY timeAt ASC")
    fun observeAll(babyId: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): TodoEntity?

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getById(id: Long): TodoEntity?

    @Query("SELECT * FROM todo")
    suspend fun getAllAll(): List<TodoEntity>

    @Query(
        "SELECT * FROM todo WHERE remindEnabled = 1 AND completed = 0 " +
            "AND (remindedAt = 0 OR remindedAt < timeAt)"
    )
    suspend fun pendingReminders(): List<TodoEntity>

    @Insert
    suspend fun insert(todo: TodoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(todos: List<TodoEntity>)

    @Query("DELETE FROM todo WHERE babyId = :babyId")
    suspend fun deleteByBabyId(babyId: Long)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
