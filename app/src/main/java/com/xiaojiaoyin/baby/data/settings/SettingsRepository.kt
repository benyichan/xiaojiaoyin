package com.xiaojiaoyin.baby.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val keyCurrentBaby = longPreferencesKey("current_baby_id")
    private val keyTypeVisibility = stringPreferencesKey("record_type_visibility")
    private val keyFeedingTimerStart = longPreferencesKey("feeding_timer_start")

    val currentBabyId: Flow<Long?> = context.dataStore.data.map { it[keyCurrentBaby] }

    /** 喂养计时开始时间戳；null = 未在计时 */
    val feedingTimerStart: Flow<Long?> = context.dataStore.data.map { it[keyFeedingTimerStart] }

    suspend fun startFeedingTimer() {
        context.dataStore.edit { it[keyFeedingTimerStart] = System.currentTimeMillis() }
    }

    suspend fun stopFeedingTimer() {
        context.dataStore.edit { it.remove(keyFeedingTimerStart) }
    }

    suspend fun setCurrentBaby(id: Long) {
        context.dataStore.edit { it[keyCurrentBaby] = id }
    }

    suspend fun getCurrentBabyId(): Long? = currentBabyId.first()

    /** 兜底：设置里没有当前宝宝时取第一个宝宝 */
    suspend fun resolveCurrentBabyId(
        babyRepo: com.xiaojiaoyin.baby.data.repository.BabyRepository
    ): Long? = getCurrentBabyId() ?: babyRepo.getAll().firstOrNull()?.id

    /** 类型显示覆盖："follow"（默认）/ "always" / "hidden" */
    val typeVisibility: Flow<Map<String, String>> =
        context.dataStore.data.map { prefs ->
            (prefs[keyTypeVisibility] ?: "").split(",")
                .filter { it.isNotBlank() }
                .associate {
                    val parts = it.split("=")
                    parts[0] to parts.getOrElse(1) { "follow" }
                }
        }

    suspend fun setTypeVisibility(type: String, mode: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[keyTypeVisibility] ?: "")
                .split(",")
                .filter { it.isNotBlank() }
                .associate {
                    val parts = it.split("=")
                    parts[0] to parts.getOrElse(1) { "follow" }
                }
                .toMutableMap()
            current[type] = mode
            prefs[keyTypeVisibility] = current.entries.joinToString(",") { "${it.key}=${it.value}" }
        }
    }
}
