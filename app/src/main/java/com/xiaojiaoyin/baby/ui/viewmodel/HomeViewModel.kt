package com.xiaojiaoyin.baby.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.data.repository.BabyRepository
import com.xiaojiaoyin.baby.data.repository.RecordRepository
import com.xiaojiaoyin.baby.data.settings.SettingsRepository
import com.xiaojiaoyin.baby.domain.AnniversaryCalc
import com.xiaojiaoyin.baby.domain.BabyAge
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
    /** 最近的纪念日（名称 to 倒计时文案）；null = 无 */
    val nextAnniversary: Pair<String, String>? = null,
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
                        settings.typeVisibility,
                        AppGraph.anniversaryRepository.observeByBaby(baby.id)
                    ) { records, visibility, anniversaries ->
                        val now = System.currentTimeMillis()
                        val ageMonths = FeedRules.ageMonths(baby.birthDateTime, now)
                        val filtered = records.filter { r ->
                            when (visibility[r.type.name] ?: "follow") {
                                "always" -> true
                                "hidden" -> false
                                else -> !isAutoHidden(baby.id, r.type, ageMonths, now)
                            }
                        }
                        val next = anniversaries
                            .map { it to AnniversaryCalc.daysUntil(it.dateAt, it.repeatYearly, now) }
                            .filter { it.second >= 0 }
                            .minByOrNull { it.second }
                        HomeUiState(
                            babies = babies,
                            currentBaby = baby,
                            derived = LunarCalculator.computeDerivedInfo(baby.birthDateTime, now),
                            ageText = ageText(baby.birthDateTime, now),
                            feedRecords = filtered,
                            nextAnniversary = next?.let { (a, d) ->
                                a.name to AnniversaryCalc.countdownText(a.dateAt, a.repeatYearly, now)
                            },
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

    private fun ageText(birthMillis: Long, now: Long): String = BabyAge.text(birthMillis, now)
}
