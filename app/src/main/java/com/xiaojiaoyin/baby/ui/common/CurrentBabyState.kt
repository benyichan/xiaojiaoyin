package com.xiaojiaoyin.baby.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import kotlinx.coroutines.flow.combine

/**
 * 观察当前宝宝：设置里的 currentBabyId，为空时兜底到第一个宝宝。
 * 切换宝宝后所有使用此状态的页面会自动跟随刷新。
 */
@Composable
fun rememberCurrentBabyId(): State<Long?> {
    return combine(
        AppGraph.settingsRepository.currentBabyId,
        AppGraph.babyRepository.observeAll()
    ) { id, babies -> id ?: babies.firstOrNull()?.id }
        .collectAsStateWithLifecycle(initialValue = null)
}
