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
    // remember 固定 Flow 实例：combine 每次重组新建实例会导致 collectAsStateWithLifecycle
    // 反复取消并重启收集，状态瞬时闪回 initialValue
    val flow = remember {
        combine(
            AppGraph.proStatusRepository.isPro,
            AppGraph.proStatusRepository.proExpireAt,
            AppGraph.proStatusRepository.trialStartAt
        ) { isPro, expireAt, trialStart ->
            val now = System.currentTimeMillis()
            // expireAt == 0L 表示永久（买断/赠送码）；月卡/年卡到期后回落为非 Pro。
            // 注意：now 在 Flow 发射时取值，到期切换在下次启动或状态变更时生效
            val proValid = isPro && (expireAt == 0L || now < expireAt)
            val trialValid = trialStart > 0 &&
                now < trialStart + ProStatusRepository.TRIAL_DAYS * ProStatusRepository.DAY_MS
            proValid || trialValid
        }
    }
    return flow.collectAsStateWithLifecycle(initialValue = false)
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
