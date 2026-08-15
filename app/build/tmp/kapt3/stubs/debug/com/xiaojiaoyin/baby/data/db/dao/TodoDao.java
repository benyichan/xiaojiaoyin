package com.xiaojiaoyin.baby.data.db.dao;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H'J\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\t\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u0011\u00c0\u0006\u0003"}, d2 = {"Lcom/xiaojiaoyin/baby/data/db/dao/TodoDao;", "", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/xiaojiaoyin/baby/data/db/entity/TodoEntity;", "babyId", "", "pendingReminders", "now", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "todo", "(Lcom/xiaojiaoyin/baby/data/db/entity/TodoEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "delete", "app_debug"})
@androidx.room.Dao()
public abstract interface TodoDao {
    
    @androidx.room.Query(value = "SELECT * FROM todo WHERE babyId = :babyId ORDER BY timeAt ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.xiaojiaoyin.baby.data.db.entity.TodoEntity>> observeAll(long babyId);
    
    @androidx.room.Query(value = "SELECT * FROM todo WHERE remindEnabled = 1 AND completed = 0 AND timeAt > :now")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object pendingReminders(long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.xiaojiaoyin.baby.data.db.entity.TodoEntity>> $completion);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.TodoEntity todo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.TodoEntity todo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.TodoEntity todo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}