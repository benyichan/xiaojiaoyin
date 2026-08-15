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

    val isPro: Flow<Boolean> = context.proDataStore.data.map { it[keyIsPro] ?: false }

    val proExpireAt: Flow<Long> = context.proDataStore.data.map { it[keyExpireAt] ?: 0L }

    val promoEndAt: Flow<Long> = context.proDataStore.data.map { it[keyPromoEndAt] ?: 0L }

    suspend fun setPromoEndAt(endAt: Long) {
        context.proDataStore.edit { it[keyPromoEndAt] = endAt }
    }

    suspend fun setProWithExpire(active: Boolean, expireAt: Long) {
        context.proDataStore.edit {
            it[keyIsPro] = active
            it[keyExpireAt] = expireAt
        }
    }
}
