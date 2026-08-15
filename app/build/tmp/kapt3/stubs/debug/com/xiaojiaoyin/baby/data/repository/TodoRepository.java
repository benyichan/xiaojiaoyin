package com.xiaojiaoyin.baby.data.repository;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\n\u001a\u00020\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\r\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ.\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/xiaojiaoyin/baby/data/repository/TodoRepository;", "", "dao", "Lcom/xiaojiaoyin/baby/data/db/dao/TodoDao;", "<init>", "(Lcom/xiaojiaoyin/baby/data/db/dao/TodoDao;)V", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/xiaojiaoyin/baby/data/db/entity/TodoEntity;", "babyId", "", "pendingReminders", "now", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "add", "title", "", "timeAt", "remindEnabled", "", "(JLjava/lang/String;JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "", "todo", "(Lcom/xiaojiaoyin/baby/data/db/entity/TodoEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "app_debug"})
public final class TodoRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.xiaojiaoyin.baby.data.db.dao.TodoDao dao = null;
    
    public TodoRepository(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.dao.TodoDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.xiaojiaoyin.baby.data.db.entity.TodoEntity>> observeAll(long babyId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object pendingReminders(long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.xiaojiaoyin.baby.data.db.entity.TodoEntity>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object add(long babyId, @org.jetbrains.annotations.NotNull()
    java.lang.String title, long timeAt, boolean remindEnabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.TodoEntity todo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.TodoEntity todo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}