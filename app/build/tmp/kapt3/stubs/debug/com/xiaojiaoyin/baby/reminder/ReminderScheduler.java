package com.xiaojiaoyin.baby.reminder;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/xiaojiaoyin/baby/reminder/ReminderScheduler;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "schedule", "", "todo", "Lcom/xiaojiaoyin/baby/data/db/entity/TodoEntity;", "cancel", "todoId", "", "buildPendingIntent", "Landroid/app/PendingIntent;", "title", "", "app_debug"})
public final class ReminderScheduler {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    public ReminderScheduler(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void schedule(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.db.entity.TodoEntity todo) {
    }
    
    public final void cancel(long todoId) {
    }
    
    private final android.app.PendingIntent buildPendingIntent(long todoId, java.lang.String title) {
        return null;
    }
}