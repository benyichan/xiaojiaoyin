package com.xiaojiaoyin.baby.data.db.dao;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\b2\u0006\u0010\n\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\fH'J\u001c\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\b2\u0006\u0010\n\u001a\u00020\u0003H'J,\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\t2\u0006\u0010\n\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0012J \u0010\u0013\u001a\u0004\u0018\u00010\u00052\u0006\u0010\n\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u000e\u0010\u0015\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017\u00c0\u0006\u0003"}, d2 = {"Lcom/xiaojiaoyin/baby/data/db/dao/RecordDao;", "", "insert", "", "record", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordEntity;", "(Lcom/xiaojiaoyin/baby/data/db/entity/RecordEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeRecent", "Lkotlinx/coroutines/flow/Flow;", "", "babyId", "limit", "", "observeAll", "countByTypeSince", "type", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordType;", "since", "(JLcom/xiaojiaoyin/baby/data/db/entity/RecordType;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lastOfType", "(JLcom/xiaojiaoyin/baby/data/db/entity/RecordType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "countAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface RecordDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordEntity record, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM record WHERE babyId = :babyId ORDER BY occurredAt DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity>> observeRecent(long babyId, int limit);
    
    @androidx.room.Query(value = "SELECT * FROM record WHERE babyId = :babyId ORDER BY occurredAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity>> observeAll(long babyId);
    
    @androidx.room.Query(value = "SELECT * FROM record WHERE babyId = :babyId AND type = :type AND occurredAt >= :since ORDER BY occurredAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countByTypeSince(long babyId, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, long since, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM record WHERE babyId = :babyId AND type = :type ORDER BY occurredAt DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object lastOfType(long babyId, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.xiaojiaoyin.baby.data.db.entity.RecordEntity> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM record")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object countAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}