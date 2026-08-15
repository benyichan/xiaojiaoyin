# 小脚印 · 宝宝成长记录 App（M1 + M2）

完全离线的本地宝宝成长记录 Android App。M1：骨架 + 宝宝档案 + 首页动态流 + 备份恢复 + 待办提醒；M2：相册 + 成长时间轴。

## 技术栈

- Kotlin 2.2.21 + Jetpack Compose + Material 3
- Navigation 3（1.1.0）
- Room 2.8.2 + DataStore 1.1.7
- lunar-java（`cn.6tail:lunar:1.7.7`）农历/八字
- AGP 8.13.2 / Gradle 8.14.3 / compileSdk 36 / minSdk 26

> 版本说明：2026.08 的新版依赖（Compose BOM 2026.08、core-ktx 1.19 等）要求 AGP 9.1 + compileSdk 37，本机 SDK 最高 36，因此选用 2025.12 代稳定组合。

## 已实现

### M2：相册与成长时间轴

- 相册：从文件选择器添加照片（自动压缩到 1600px 存私有目录）、按月分组照片墙、备注角标、点开预览、编辑备注、删除（连带文件）、按备注搜索
- 成长时间轴：重要节点（标题/日期/时间/备注）、时间轴样式（金色节点 + 卡片）、⭐ 重要徽章、按月分组、按标题/备注搜索、详情查看、删除
- 首页动态流新增相册/节点卡片，首页"拍照"快捷可直接选图添加照片

### M1

- 宝宝档案：姓名/小名/性别/出生日期（精确到时分），自动计算生肖、星座、农历、八字四柱、阳历/农历生日倒计时
- 首页：宝宝信息卡、2×2 信息网格、八字、生日双卡、快捷记录（喂养/哭闹/生长）、最近动态流（含月龄淡出规则）
- 多宝宝切换（设置页自动选当前）
- 待办清单：增删、完成、提醒开关；AlarmManager 精确闹钟；通知渠道；权限引导（通知/精确闹钟/电池优化/国产 ROM 文案）；重启恢复
- 备份恢复：SAF 导出 zip（SQLite WAL 三件套 + manifest），恢复校验并重启
- 备份恢复现在包含 DataStore 设置文件（当前宝宝等）
- 单测：农历/八字/生日倒计时、BMI、月龄规则（12 个用例全绿）

### M2 修复的 bug

- Room 枚举扩展（PHOTO/NODE）后 kapt 增量缓存导致运行崩溃 → clean 重新生成
- 备份恢复后 DataStore 的 currentBabyId 丢失导致相册/成长/待办读不到宝宝 → 各页面改为"设置优先 + 第一个宝宝兜底"，备份包含设置文件

## 验收记录（2026-08-15，模拟器 Medium_Phone_API_36）

1. 添加宝宝 → 首页派生信息正确（2026-08-15 出生：马、狮子座、七月初三、丙午年 · 丙申月 · 辛酉日 · 丁酉时）✅
2. 记录喂养 → 动态流出现"喂养 · 母乳 · 15 · 8月15日 17:59" ✅
3. 待办开启提醒 → `dumpsys alarm` 确认 AlarmManager 注册（RTC_WAKEUP → ReminderReceiver）✅
4. 导出备份 → zip 含 database.db/-wal/-shm + manifest ✅
5. `pm clear` 清空 → 从备份恢复 → 宝宝与喂养记录完整回来 ✅
6. `gradlew testDebugUnitTest` 12 个用例通过 ✅
7. M2：相册选图添加 → 按月分组显示；成长添加节点 → 时间轴 + ⭐ 徽章；首页动态流出现照片/节点卡片；搜索过滤与空结果；节点删除 ✅

## 已知问题 / 待办

- 提醒"到点发通知"未在模拟器实测（playstore 镜像无法改系统时间），需真机抽查；AlarmManager 注册链路已验证
- lunar 库 `getShengxiao()` 标记 deprecated，生肖显示正常，后续版本可换新 API
- 新建待办默认时间为明天同时刻（M1 简化，未做日期选择器）
- 出生日期默认当前时间，用户可改
- 国产 ROM 权限引导为文案 + 标准设置跳转，真机矩阵在 M5 上架前验证
- 相机直拍（TakePicture + FileProvider）未做，照片走文件选择器；真机拍照体验后续优化
- 节点暂不支持附带照片
- M3+：统计看板（生长曲线/喂养统计）、医疗/学籍/好物/那年今日、Wi-Fi 共享、IAP

## 构建

```bash
gradlew.bat assembleDebug        # 构建 APK
gradlew.bat testDebugUnitTest    # 单测
```

APK 输出：`app/build/outputs/apk/debug/app-debug.apk`
