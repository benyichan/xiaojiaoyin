package com.xiaojiaoyin.baby.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.repository.BabyRepository
import com.xiaojiaoyin.baby.data.repository.RecordRepository
import com.xiaojiaoyin.baby.data.settings.SettingsRepository
import com.xiaojiaoyin.baby.domain.FeedRules
import com.xiaojiaoyin.baby.domain.LunarCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val babies: List<BabyEntity> = emptyList(),
    val currentBaby: BabyEntity? = null,
    val derived: com.xiaojiaoyin.baby.domain.DerivedInfo? = null,
    val ageText: String = "",
    val feedRecords: List<RecordEntity> = emptyList(),
    val loading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val babyRepo: BabyRepository,
    private val recordRepo: RecordRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = settings.currentBabyId
        .flatMapLatest { currentId ->
            babyRepo.observeAll().flatMapLatest { babies ->
                val baby = babies.firstOrNull { it.id == currentId } ?: babies.firstOrNull()
                if (baby == null) {
                    flowOf(HomeUiState(babies = babies, loading = false))
                } else {
                    combine(
                        recordRepo.observeRecent(baby.id, limit = 30),
                        settings.typeVisibility
                    ) { records, visibility ->
                        val now = System.currentTimeMillis()
                        val ageMonths = FeedRules.ageMonths(baby.birthDateTime, now)
                        val filtered = records.filter { r ->
                            when (visibility[r.type.name] ?: "follow") {
                                "always" -> true
                                "hidden" -> false
                                else -> !isAutoHidden(baby.id, r.type, ageMonths, now)
                            }
                        }
                        HomeUiState(
                            babies = babies,
                            currentBaby = baby,
                            derived = LunarCalculator.computeDerivedInfo(baby.birthDateTime, now),
                            ageText = ageText(baby.birthDateTime, now),
                            feedRecords = filtered,
                            loading = false
                        )
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    private suspend fun isAutoHidden(
        babyId: Long,
        type: RecordType,
        ageMonths: Int,
        now: Long
    ): Boolean {
        val last = recordRepo.lastOfType(babyId, type)?.occurredAt
        return FeedRules.shouldAutoHide(type, ageMonths, last, now)
    }

    private fun ageText(birthMillis: Long, now: Long): String {
        val months = FeedRules.ageMonths(birthMillis, now)
        return when {
            months < 1 -> "出生不到 1 个月"
            months < 24 -> {
                val years = months / 12
                val rest = months % 12
                if (years == 0) "${months}个月" else "${years}岁${rest}个月"
            }
            else -> {
                val years = months / 12
                "${years}岁"
            }
        }
    }
}
