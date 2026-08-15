package com.xiaojiaoyin.baby.ui.screens;

@kotlin.Metadata(mv = {2, 2, 0}, k = 2, xi = 48, d1 = {"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aH\u0010\u0000\u001a\u00020\u00012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a\u0016\u0010\u0007\u001a\u00020\u00012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a(\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\"\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0003\u001a\u0010\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0016H\u0002\u001a\u0012\u0010\u0017\u001a\u00020\u00012\b\u0010\u0018\u001a\u0004\u0018\u00010\rH\u0003\u001a9\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001d2\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0003\u00a2\u0006\u0004\b\u001e\u0010\u001f\u001a2\u0010 \u001a\u00020\u00012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a7\u0010!\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001d2\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0003\u00a2\u0006\u0004\b#\u0010$\u001a\u0010\u0010%\u001a\u00020\u00012\u0006\u0010&\u001a\u00020'H\u0003\u001a\u0010\u0010(\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0016H\u0002\u00a8\u0006)"}, d2 = {"HomeScreen", "", "onEdit", "Lkotlin/Function0;", "onFeeding", "onCrying", "onGrowth", "EmptyHome", "onAdd", "InfoCard", "baby", "Lcom/xiaojiaoyin/baby/data/db/entity/BabyEntity;", "derivedText", "Lcom/xiaojiaoyin/baby/domain/DerivedInfo;", "InfoCell", "label", "", "value", "modifier", "Landroidx/compose/ui/Modifier;", "formatBirth", "millis", "", "BirthdayCards", "derived", "BirthdayCard", "days", "date", "color", "Landroidx/compose/ui/graphics/Color;", "BirthdayCard-42QJj7c", "(Ljava/lang/String;JLjava/lang/String;JLandroidx/compose/ui/Modifier;)V", "QuickActions", "QuickItem", "onClick", "QuickItem-RPmYEkk", "(Ljava/lang/String;JLkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;)V", "FeedCard", "record", "Lcom/xiaojiaoyin/baby/data/db/entity/RecordEntity;", "formatTime", "app_debug"})
public final class HomeScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFeeding, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCrying, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onGrowth) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyHome(kotlin.jvm.functions.Function0<kotlin.Unit> onAdd) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InfoCard(com.xiaojiaoyin.baby.data.db.entity.BabyEntity baby, com.xiaojiaoyin.baby.domain.DerivedInfo derivedText, kotlin.jvm.functions.Function0<kotlin.Unit> onEdit) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InfoCell(java.lang.String label, java.lang.String value, androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.lang.String formatBirth(long millis) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BirthdayCards(com.xiaojiaoyin.baby.domain.DerivedInfo derived) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QuickActions(kotlin.jvm.functions.Function0<kotlin.Unit> onFeeding, kotlin.jvm.functions.Function0<kotlin.Unit> onCrying, kotlin.jvm.functions.Function0<kotlin.Unit> onGrowth) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FeedCard(com.xiaojiaoyin.baby.data.db.entity.RecordEntity record) {
    }
    
    private static final java.lang.String formatTime(long millis) {
        return null;
    }
}