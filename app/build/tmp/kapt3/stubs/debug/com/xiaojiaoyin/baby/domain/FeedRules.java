package com.xiaojiaoyin.baby.domain;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u0005J\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\nJ/\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\n2\b\u0010\u0014\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0016"}, d2 = {"Lcom/xiaojiaoyin/baby/domain/FeedRules;", "", "<init>", "()V", "DAY_MS", "", "AUTO_HIDE_GAP_DAYS", "ACTIVE_MONTHS", "", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordType;", "", "getACTIVE_MONTHS", "()Ljava/util/Map;", "ageMonths", "birthDateTimeMillis", "now", "isActive", "", "type", "shouldAutoHide", "lastRecordAt", "(Lcom/xiaojiaoyin/baby/data/db/entity/RecordType;ILjava/lang/Long;J)Z", "app_debug"})
public final class FeedRules {
    private static final long DAY_MS = 86400000L;
    public static final long AUTO_HIDE_GAP_DAYS = 60L;
    
    /**
     * 各类型活跃月龄上限；不在表中的类型永远活跃
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<com.xiaojiaoyin.baby.data.db.entity.RecordType, java.lang.Integer> ACTIVE_MONTHS = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.domain.FeedRules INSTANCE = null;
    
    private FeedRules() {
        super();
    }
    
    /**
     * 各类型活跃月龄上限；不在表中的类型永远活跃
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<com.xiaojiaoyin.baby.data.db.entity.RecordType, java.lang.Integer> getACTIVE_MONTHS() {
        return null;
    }
    
    public final int ageMonths(long birthDateTimeMillis, long now) {
        return 0;
    }
    
    public final boolean isActive(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, int ageMonths) {
        return false;
    }
    
    public final boolean shouldAutoHide(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.RecordType type, int ageMonths, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastRecordAt, long now) {
        return false;
    }
}