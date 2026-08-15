# 小脚印 · 宝宝成长记录 App（M1）

完全离线的本地宝宝成长记录 Android App。M1 阶段目标：可运行的骨架 + 宝宝档案 + 首页动态流 + 备份恢复 + 可靠的一次性待办提醒。

## 技术栈

- Kotlin 2.2.21 + Jetpack Compose + Material 3
- Navigation 3（1.1.0）
- Room 2.8.2 + DataStore 1.1.7
- lunar-java（`cn.6tail:lunar:1.7.7`）农历/八字
- AGP 8.13.2 / Gradle 8.14.3 / compileSdk 36 / minSdk 26

> 版本说明：2026.08 的新版依赖（Compose BOM 2026.08、core-ktx 1.19 等）要求 AGP 9.1 + compileSdk 37，本机 SDK 最高 36，因此选用 2025.12 代稳定组合。

## 已实现（M1）

- 宝宝档案：姓名/小名/性别/出生日期（精确到时分），自动计算生肖、星座、农历、八字四柱、阳历/农历生日倒计时
- 首页：宝宝信息卡、2×2 信息网格、八字、生日双卡、快捷记录（喂养/哭闹/生长）、最近动态流（含月龄淡出规则）
- 多宝宝切换（设置页自动选当前）
- 待办清单：增删、完成、提醒开关；AlarmManager 精确闹钟；通知渠道；权限引导（通知/精确闹钟/电池优化/国产 ROM 文案）；重启恢复
- 备份恢复：SAF 导出 zip（SQLite WAL 三件套 + manifest），恢复校验并重启
- 单测：农历/八字/生日倒计时、BMI、月龄规则（12 个用例全绿）

## 验收记录（2026-08-15，模拟器 Medium_Phone_API_36）

1. 添加宝宝 → 首页派生信息正确（2026-08-15 出生：马、狮子座、七月初三、丙午年 · 丙申月 · 辛酉日 · 丁酉时）✅
2. 记录喂养 → 动态流出现"喂养 · 母乳 · 15 · 8月15日 17:59" ✅
3. 待办开启提醒 → `dumpsys alarm` 确认 AlarmManager 注册（RTC_WAKEUP → ReminderReceiver）✅
4. 导出备份 → zip 含 database.db/-wal/-shm + manifest ✅
5. `pm clear` 清空 → 从备份恢复 → 宝宝与喂养记录完整回来 ✅
6. `gradlew testDebugUnitTest` 12 个用例通过 ✅

## 已知问题 / 待办

- 提醒"到点发通知"未在模拟器实测（playstore 镜像无法改系统时间），需真机抽查；AlarmManager 注册链路已验证
- lunar 库 `getShengxiao()` 标记 deprecated，生肖显示正常，后续版本可换新 API
- 新建待办默认时间为明天同时刻（M1 简化，未做日期选择器）
- 出生日期默认当前时间，用户可改
- 国产 ROM 权限引导为文案 + 标准设置跳转，真机矩阵在 M5 上架前验证
- 拍照快捷入口 M1 为占位（M2 相册实现）
- M2+：相册、成长时间轴、统计看板、医疗/学籍/好物/那年今日、Wi-Fi 共享、IAP

## 构建

```bash
gradlew.bat assembleDebug        # 构建 APK
gradlew.bat testDebugUnitTest    # 单测
```

APK 输出：`app/build/outputs/apk/debug/app-debug.apk`
