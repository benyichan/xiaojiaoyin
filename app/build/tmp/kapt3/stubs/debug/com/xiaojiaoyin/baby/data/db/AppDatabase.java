package com.xiaojiaoyin.baby.data.db;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lcom/xiaojiaoyin/baby/data/db/AppDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "babyDao", "Lcom/xiaojiaoyin/baby/data/db/dao/BabyDao;", "recordDao", "Lcom/xiaojiaoyin/baby/data/db/dao/RecordDao;", "todoDao", "Lcom/xiaojiaoyin/baby/data/db/dao/TodoDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.xiaojiaoyin.baby.data.db.entity.BabyEntity.class, com.xiaojiaoyin.baby.data.db.entity.RecordEntity.class, com.xiaojiaoyin.baby.data.db.entity.TodoEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String NAME = "baby-app.db";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.xiaojiaoyin.baby.data.db.AppDatabase instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.data.db.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.xiaojiaoyin.baby.data.db.dao.BabyDao babyDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.xiaojiaoyin.baby.data.db.dao.RecordDao recordDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.xiaojiaoyin.baby.data.db.dao.TodoDao todoDao();
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/xiaojiaoyin/baby/data/db/AppDatabase$Companion;", "", "<init>", "()V", "NAME", "", "instance", "Lcom/xiaojiaoyin/baby/data/db/AppDatabase;", "get", "context", "Landroid/content/Context;", "closeForRestore", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.xiaojiaoyin.baby.data.db.AppDatabase get(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        /**
         * 备份恢复专用：关闭并清空单例，供恢复后重启进程使用
         */
        public final void closeForRestore() {
        }
    }
}