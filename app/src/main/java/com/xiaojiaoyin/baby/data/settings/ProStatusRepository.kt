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

    val isPro: Flow<Boolean> = context.proDataStore.data.map { it[keyIsPro] ?: false }

    val proExpireAt: Flow<Long> = context.proDataStore.data.map { it[keyExpireAt] ?: 0L }

    suspend fun setProWithExpire(active: Boolean, expireAt: Long) {
        context.proDataStore.edit {
            it[keyIsPro] = active
            it[keyExpireAt] = expireAt
        }
    }
}
