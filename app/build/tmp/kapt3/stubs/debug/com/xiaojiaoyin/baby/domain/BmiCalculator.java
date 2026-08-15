package com.xiaojiaoyin.baby.domain;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005\u00a8\u0006\n"}, d2 = {"Lcom/xiaojiaoyin/baby/domain/BmiCalculator;", "", "<init>", "()V", "bmi", "", "weightKg", "heightCm", "evaluate", "", "app_debug"})
public final class BmiCalculator {
    @org.jetbrains.annotations.NotNull()
    public static final com.xiaojiaoyin.baby.domain.BmiCalculator INSTANCE = null;
    
    private BmiCalculator() {
        super();
    }
    
    public final double bmi(double weightKg, double heightCm) {
        return 0.0;
    }
    
    /**
     * M1 简化评估（正式 WHO 百分位曲线在 M3 引入）：
     * BMI < 14 偏低，14-18 正常，>18 偏高。
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String evaluate(double weightKg, double heightCm) {
        return null;
    }
}