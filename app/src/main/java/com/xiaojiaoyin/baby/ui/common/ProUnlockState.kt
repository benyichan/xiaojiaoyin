package com.xiaojiaoyin.baby.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.settings.ProStatusRepository
import kotlinx.coroutines.flow.combine

/** Pro 会员或 7 天试用期内均为解锁状态 */
@Composable
fun rememberProUnlocked(): State<Boolean> {
    return combine(
        AppGraph.proStatusRepository.isPro,
        AppGraph.proStatusRepository.trialStartAt
    ) { isPro, trialStart ->
        isPro || (
            trialStart > 0 &&
                System.currentTimeMillis() <
                trialStart + ProStatusRepository.TRIAL_DAYS * ProStatusRepository.DAY_MS
            )
    }.collectAsStateWithLifecycle(initialValue = false)
}

/** 试用剩余天数（0 表示试用已结束或未开始） */
@Composable
fun rememberTrialRemainingDays(): State<Long> {
    val trialStart by AppGraph.proStatusRepository.trialStartAt
        .collectAsStateWithLifecycle(initialValue = 0L)
    return remember(trialStart) {
        mutableLongStateOf(
            if (trialStart == 0L) 0L
            else ((trialStart + ProStatusRepository.TRIAL_DAYS * ProStatusRepository.DAY_MS - System.currentTimeMillis())
                / ProStatusRepository.DAY_MS).coerceAtLeast(0)
        )
    }
}
