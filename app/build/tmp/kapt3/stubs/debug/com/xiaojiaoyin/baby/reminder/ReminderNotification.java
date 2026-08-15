package com.xiaojiaoyin.baby.reminder;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u001e\u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/xiaojiaoyin/baby/reminder/ReminderNotification;", "", "<init>", "()V", "CHANNEL_ID", "", "ensureChannel", "", "context", "Landroid/content/Context;", "show", "todoId", "", "title", "app_debug"})
public final class ReminderNotification {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CHANNEL_ID = "reminder";
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.reminder.ReminderNotification INSTANCE = null;
    
    private ReminderNotification() {
        super();
    }
    
    public final void ensureChannel(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void show(@org.jetbrains.annotations.NotNull()
    android.content.Context context, long todoId, @org.jetbrains.annotations.NotNull()
    java.lang.String title) {
    }
}