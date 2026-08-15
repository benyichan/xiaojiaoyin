package com.xiaojiaoyin.baby.data.repository;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J$\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rJ\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u00072\u0006\u0010\n\u001a\u00020\u000bJ8\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0016J&\u0010\u0017\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\u0019J \u0010\u001a\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/xiaojiaoyin/baby/data/repository/RecordRepository;", "", "dao", "Lcom/xiaojiaoyin/baby/data/db/dao/RecordDao;", "<init>", "(Lcom/xiaojiaoyin/baby/data/db/dao/RecordDao;)V", "observeRecent", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordEntity;", "babyId", "", "limit", "", "observeAll", "add", "type", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordType;", "occurredAt", "detailJson", "", "note", "(JLcom/xiaojiaoyin/baby/data/db/entity/RecordType;JLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "countSince", "since", "(JLcom/xiaojiaoyin/baby/data/db/entity/RecordType;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lastOfType", "(JLcom/xiaojiaoyin/baby/data/db/entity/RecordType;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class RecordRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.xiaojiaoyin.baby.data.db.dao.RecordDao dao = null;
    
    public RecordRepository(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.dao.RecordDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity>> observeRecent(long babyId, int limit) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity>> observeAll(long babyId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object add(long babyId, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, long occurredAt, @org.jetbrains.annotations.NotNull()
    java.lang.String detailJson, @org.jetbrains.annotations.NotNull()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object countSince(long babyId, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, long since, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object lastOfType(long babyId, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.xiaojiaoyin.baby.data.db.entity.RecordEntity> $completion) {
        return null;
    }
}