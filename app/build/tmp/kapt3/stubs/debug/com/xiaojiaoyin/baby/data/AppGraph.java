package com.xiaojiaoyin.baby.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cR\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001e\u0010\n\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\t@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001e\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u0011@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u001e\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020\u0015@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006\u001d"}, d2 = {"Lcom/xiaojiaoyin/baby/data/AppGraph;", "", "<init>", "()V", "value", "Lcom/xiaojiaoyin/baby/data/db/AppDatabase;", "database", "getDatabase", "()Lcom/xiaojiaoyin/baby/data/db/AppDatabase;", "Lcom/xiaojiaoyin/baby/data/repository/BabyRepository;", "babyRepository", "getBabyRepository", "()Lcom/xiaojiaoyin/baby/data/repository/BabyRepository;", "Lcom/xiaojiaoyin/baby/data/repository/RecordRepository;", "recordRepository", "getRecordRepository", "()Lcom/xiaojiaoyin/baby/data/repository/RecordRepository;", "Lcom/xiaojiaoyin/baby/data/repository/TodoRepository;", "todoRepository", "getTodoRepository", "()Lcom/xiaojiaoyin/baby/data/repository/TodoRepository;", "Lcom/xiaojiaoyin/baby/data/settings/SettingsRepository;", "settingsRepository", "getSettingsRepository", "()Lcom/xiaojiaoyin/baby/data/settings/SettingsRepository;", "init", "", "context", "Landroid/content/Context;", "app_debug"})
public final class AppGraph {
    private static com.xiaojiaoyin.baby.data.db.AppDatabase database;
    private static com.xiaojiaoyin.baby.data.repository.BabyRepository babyRepository;
    private static com.xiaojiaoyin.baby.data.repository.RecordRepository recordRepository;
    private static com.xiaojiaoyin.baby.data.repository.TodoRepository todoRepository;
    private static com.xiaojiaoyin.baby.data.settings.SettingsRepository settingsRepository;
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.data.AppGraph INSTANCE = null;
    
    private AppGraph() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.data.db.AppDatabase getDatabase() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.data.repository.BabyRepository getBabyRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.data.repository.RecordRepository getRecordRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.data.repository.TodoRepository getTodoRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.data.settings.SettingsRepository getSettingsRepository() {
        return null;
    }
    
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}