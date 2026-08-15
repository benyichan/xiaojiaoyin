package com.xiaojiaoyin.baby.ui.viewmodel;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ.\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0012H\u0082@\u00a2\u0006\u0002\u0010\u0018J\u0018\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0012H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u001c"}, d2 = {"Lcom/xiaojiaoyin/baby/ui/viewmodel/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "babyRepo", "Lcom/xiaojiaoyin/baby/data/repository/BabyRepository;", "recordRepo", "Lcom/xiaojiaoyin/baby/data/repository/RecordRepository;", "settings", "Lcom/xiaojiaoyin/baby/data/settings/SettingsRepository;", "<init>", "(Lcom/xiaojiaoyin/baby/data/repository/BabyRepository;Lcom/xiaojiaoyin/baby/data/repository/RecordRepository;Lcom/xiaojiaoyin/baby/data/settings/SettingsRepository;)V", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/xiaojiaoyin/baby/ui/viewmodel/HomeUiState;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "isAutoHidden", "", "babyId", "", "type", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordType;", "ageMonths", "", "now", "(JLcom/xiaojiaoyin/baby/data/db/entity/RecordType;IJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ageText", "", "birthMillis", "app_debug"})
@kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.xiaojiaoyin.baby.data.repository.BabyRepository babyRepo = null;
    @org.jetbrains.annotations.NotNull()
    private final com.xiaojiaoyin.baby.data.repository.RecordRepository recordRepo = null;
    @org.jetbrains.annotations.NotNull()
    private final com.xiaojiaoyin.baby.data.settings.SettingsRepository settings = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.xiaojiaoyin.baby.ui.viewmodel.HomeUiState> uiState = null;
    
    public HomeViewModel(@org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.repository.BabyRepository babyRepo, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.repository.RecordRepository recordRepo, @org.jetbrains.annotations.NotNull()
    com.xiaojiaoyin.baby.data.settings.SettingsRepository settings) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.xiaojiaoyin.baby.ui.viewmodel.HomeUiState> getUiState() {
        return null;
    }
    
    private final java.lang.Object isAutoHidden(long babyId, com.xiaojiaoyin.baby.data.db.entity.RecordType type, int ageMonths, long now, kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    private final java.lang.String ageText(long birthMillis, long now) {
        return null;
    }
}