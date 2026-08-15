package com.xiaojiaoyin.baby.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.proDataStore by preferencesDataStore(name = "pro_status")

class ProStatusRepository(private val context: Context) {
    private val keyIsPro = booleanPreferencesKey("is_pro")
    private val keyExpireAt = longPreferencesKey("pro_expire_at")
    private val keyPromoEndAt = longPreferencesKey("promo_end_at")
    private val keyTrialStartAt = longPreferencesKey("trial_start_at")
    private val keyTrialReminderShown = booleanPreferencesKey("trial_reminder_shown")

    val isPro: Flow<Boolean> = context.proDataStore.data.map { it[keyIsPro] ?: false }

    val proExpireAt: Flow<Long> = context.proDataStore.data.map { it[keyExpireAt] ?: 0L }

    val promoEndAt: Flow<Long> = context.proDataStore.data.map { it[keyPromoEndAt] ?: 0L }

    /** 免费试用开始时间；0 表示未开始 */
    val trialStartAt: Flow<Long> = context.proDataStore.data.map { it[keyTrialStartAt] ?: 0L }

    val trialReminderShown: Flow<Boolean> =
        context.proDataStore.data.map { it[keyTrialReminderShown] ?: false }

    suspend fun setPromoEndAt(endAt: Long) {
        context.proDataStore.edit { it[keyPromoEndAt] = endAt }
    }

    suspend fun setProWithExpire(active: Boolean, expireAt: Long) {
        context.proDataStore.edit {
            it[keyIsPro] = active
            it[keyExpireAt] = expireAt
        }
    }

    /** 首次启动时记录试用开始时间（幂等） */
    suspend fun ensureTrialStarted() {
        context.proDataStore.edit {
            if (it[keyTrialStartAt] == null || it[keyTrialStartAt] == 0L) {
                it[keyTrialStartAt] = System.currentTimeMillis()
            }
        }
    }

    suspend fun setTrialReminderShown() {
        context.proDataStore.edit { it[keyTrialReminderShown] = true }
    }

    companion object {
        const val TRIAL_DAYS = 7L
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
