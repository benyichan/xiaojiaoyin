package com.xiaojiaoyin.baby.domain;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\u0005J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005H\u0002J\u0018\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/xiaojiaoyin/baby/domain/LunarCalculator;", "", "<init>", "()V", "DAY_MS", "", "zone", "Ljava/time/ZoneId;", "computeDerivedInfo", "Lcom/xiaojiaoyin/baby/domain/DerivedInfo;", "birthDateTimeMillis", "todayMillis", "nextSolarBirthday", "Lcom/xiaojiaoyin/baby/domain/BirthdayCountdown;", "nextLunarBirthday", "birthLunar", "Lcom/nlf/calendar/Lunar;", "app_debug"})
public final class LunarCalculator {
    private static final long DAY_MS = 86400000L;
    @org.jetbrains.annotations.NotNull()
    private static final java.time.ZoneId zone = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.domain.LunarCalculator INSTANCE = null;
    
    private LunarCalculator() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.domain.DerivedInfo computeDerivedInfo(long birthDateTimeMillis, long todayMillis) {
        return null;
    }
    
    private final com.xiaojiaoyin.baby.domain.BirthdayCountdown nextSolarBirthday(long birthDateTimeMillis, long todayMillis) {
        return null;
    }
    
    private final com.xiaojiaoyin.baby.domain.BirthdayCountdown nextLunarBirthday(com.nlf.calendar.Lunar birthLunar, long todayMillis) {
        return null;
    }
}