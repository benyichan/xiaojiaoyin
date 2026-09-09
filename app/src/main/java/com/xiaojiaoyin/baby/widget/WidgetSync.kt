package com.xiaojiaoyin.baby.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 数据变更后刷新桌面小组件。
 * 小组件自身只在系统轮询或被重新添加时才重算：宝宝信息/月龄/生日倒计时/今日待办数
 * 变更后必须显式触发，否则桌面显示会长期停留旧值。
 * fire-and-forget；未添加对应小组件时 updateAll 为 no-op。
 */
object WidgetSync {
    fun refresh(context: Context) {
        val app = context.applicationContext
        CoroutineScope(Dispatchers.Default).launch {
            runCatching { BabyInfoWidget.updateAll(app) }
            runCatching { BabyPhotoWidget.updateAll(app) }
        }
    }
}
