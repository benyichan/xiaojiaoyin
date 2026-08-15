package com.xiaojiaoyin.baby.reminder;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\r\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0006\u001a\u00020\u0007J\u001c\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\u0014\u00a8\u0006\u0015"}, d2 = {"Lcom/xiaojiaoyin/baby/reminder/ReminderPermissionHelper;", "", "<init>", "()V", "needsNotificationPermission", "", "context", "Landroid/content/Context;", "requestNotificationPermission", "", "activity", "Landroidx/activity/ComponentActivity;", "canScheduleExact", "openExactAlarmSettings", "isIgnoringBatteryOptimizations", "openBatterySettings", "vendorHint", "", "showGuideDialog", "onOpenSettings", "Lkotlin/Function0;", "app_debug"})
public final class ReminderPermissionHelper {
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.reminder.ReminderPermissionHelper INSTANCE = null;
    
    private ReminderPermissionHelper() {
        super();
    }
    
    public final boolean needsNotificationPermission(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void requestNotificationPermission(@org.jetbrains.annotations.NotNull()
    androidx.activity.ComponentActivity activity) {
    }
    
    public final boolean canScheduleExact(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void openExactAlarmSettings(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final boolean isIgnoringBatteryOptimizations(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void openBatterySettings(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * 国产 ROM 自启动引导文案；返回 null 表示无需提示
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String vendorHint(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final void showGuideDialog(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings) {
    }
}