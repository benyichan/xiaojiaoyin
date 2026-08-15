package com.xiaojiaoyin.baby.ui.viewmodel;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BS\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0004H\u00c6\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\tH\u00c6\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\rH\u00c6\u0003JU\u0010!\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010\"\u001a\u00020\r2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\tH\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0011R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006'"}, d2 = {"Lcom/xiaojiaoyin/baby/ui/viewmodel/HomeUiState;", "", "babies", "", "Lcom/xiaojiaoyin/baby/data/db/entity/BabyEntity;", "currentBaby", "derived", "Lcom/xiaojiaoyin/baby/domain/DerivedInfo;", "ageText", "", "feedRecords", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordEntity;", "loading", "", "<init>", "(Ljava/util/List;Lcom/xiaojiaoyin/baby/data/db/entity/BabyEntity;Lcom/xiaojiaoyin/baby/domain/DerivedInfo;Ljava/lang/String;Ljava/util/List;Z)V", "getBabies", "()Ljava/util/List;", "getCurrentBaby", "()Lcom/xiaojiaoyin/baby/data/db/entity/BabyEntity;", "getDerived", "()Lcom/xiaojiaoyin/baby/domain/DerivedInfo;", "getAgeText", "()Ljava/lang/String;", "getFeedRecords", "getLoading", "()Z", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class HomeUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.xiaojiaoyin.baby.data.db.entity.BabyEntity> babies = null;
    @org.jetbrains.annotations.Nullable()
    private final com.xiaojiaoyin.baby.data.db.entity.BabyEntity currentBaby = null;
    @org.jetbrains.annotations.Nullable()
    private final com.xiaojiaoyin.baby.domain.DerivedInfo derived = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ageText = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity> feedRecords = null;
    private final boolean loading = false;
    
    public HomeUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.xiaojiaoyin.baby.data.db.entity.BabyEntity> babies, @org.jetbrains.annotations.Nullable()
    com.xiaojiaoyin.baby.data.db.entity.BabyEntity currentBaby, @org.jetbrains.annotations.Nullable()
    com.xiaojiaoyin.baby.domain.DerivedInfo derived, @org.jetbrains.annotations.NotNull()
    java.lang.String ageText, @org.jetbrains.annotations.NotNull()
    java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity> feedRecords, boolean loading) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.xiaojiaoyin.baby.data.db.entity.BabyEntity> getBabies() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.xiaojiaoyin.baby.data.db.entity.BabyEntity getCurrentBaby() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.xiaojiaoyin.baby.domain.DerivedInfo getDerived() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAgeText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity> getFeedRecords() {
        return null;
    }
    
    public final boolean getLoading() {
        return false;
    }
    
    public HomeUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.xiaojiaoyin.baby.data.db.entity.BabyEntity> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.xiaojiaoyin.baby.data.db.entity.BabyEntity component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.xiaojiaoyin.baby.domain.DerivedInfo component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity> component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.xiaojiaoyin.baby.ui.viewmodel.HomeUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.xiaojiaoyin.baby.data.db.entity.BabyEntity> babies, @org.jetbrains.annotations.Nullable()
    com.xiaojiaoyin.baby.data.db.entity.BabyEntity currentBaby, @org.jetbrains.annotations.Nullable()
    com.xiaojiaoyin.baby.domain.DerivedInfo derived, @org.jetbrains.annotations.NotNull()
    java.lang.String ageText, @org.jetbrains.annotations.NotNull()
    java.util.List<com.xiaojiaoyin.baby.data.db.entity.RecordEntity> feedRecords, boolean loading) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}