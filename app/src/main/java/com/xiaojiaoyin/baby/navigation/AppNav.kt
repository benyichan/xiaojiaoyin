package com.xiaojiaoyin.baby.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.xiaojiaoyin.baby.ui.screens.AlbumScreen
import com.xiaojiaoyin.baby.ui.screens.AnnivScreen
import com.xiaojiaoyin.baby.ui.screens.AboutScreen
import com.xiaojiaoyin.baby.ui.screens.BabyManageScreen
import com.xiaojiaoyin.baby.ui.screens.BabyDetailScreen
import com.xiaojiaoyin.baby.ui.screens.BackupScreen
import com.xiaojiaoyin.baby.ui.screens.BabyEditScreen
import com.xiaojiaoyin.baby.ui.screens.CryingZoneScreen
import com.xiaojiaoyin.baby.ui.screens.CryingFormScreen
import com.xiaojiaoyin.baby.ui.screens.FeedingZoneScreen
import com.xiaojiaoyin.baby.ui.screens.FeedingFormScreen
import com.xiaojiaoyin.baby.ui.screens.GoodsFormScreen
import com.xiaojiaoyin.baby.ui.screens.GoodsScreen
import com.xiaojiaoyin.baby.ui.screens.GrowthFormScreen
import com.xiaojiaoyin.baby.ui.screens.GrowthChartScreen
import com.xiaojiaoyin.baby.ui.screens.GrowthScreen
import com.xiaojiaoyin.baby.ui.screens.HomeScreen
import com.xiaojiaoyin.baby.ui.screens.PrivacyPolicyScreen
import com.xiaojiaoyin.baby.ui.screens.MedicalFormScreen
import com.xiaojiaoyin.baby.ui.screens.MedicalScreen
import com.xiaojiaoyin.baby.ui.screens.MineScreen
import com.xiaojiaoyin.baby.ui.screens.NodeFormScreen
import com.xiaojiaoyin.baby.ui.screens.PurchaseScreen
import com.xiaojiaoyin.baby.ui.screens.ReminderSettingsScreen
import com.xiaojiaoyin.baby.ui.screens.SchoolFormScreen
import com.xiaojiaoyin.baby.ui.screens.SchoolScreen
import com.xiaojiaoyin.baby.ui.screens.StatsScreen
import com.xiaojiaoyin.baby.ui.screens.SyncScreen
import com.xiaojiaoyin.baby.ui.screens.TodoScreen
import com.xiaojiaoyin.baby.ui.theme.Bg
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextSecondary

// ===== 路由 =====
sealed interface Route {
    data object Home : Route
    data object Growth : Route
    data object Album : Route
    data object Stats : Route
    data object Mine : Route
    data class EditBaby(val babyId: Long? = null) : Route
    data object Feeding : Route
    data object Crying : Route
    data object GrowthForm : Route
    data object Todo : Route
    data object Backup : Route
    data class NodeForm(val nodeId: Long? = null) : Route
    data object GrowthChart : Route
    data object Medical : Route
    data object MedicalForm : Route
    data object FeedingZone : Route
    data object CryingZone : Route
    data object School : Route
    data object SchoolForm : Route
    data object Goods : Route
    data object GoodsForm : Route
    data object Anniv : Route
    data object Sync : Route
    data object Purchase : Route
    data object BabyManage : Route
    data object About : Route
    data object Privacy : Route
    data object ReminderSettings : Route
    data class BabyDetail(val babyId: Long) : Route
}

private val TABS = listOf(
    Route.Home to "首页",
    Route.Growth to "成长",
    Route.Album to "相册",
    Route.Stats to "统计",
    Route.Mine to "我的"
)

@Composable
fun AppNav() {
    val backStack = remember { mutableStateListOf<Any>(Route.Home) }
    val top = backStack.lastOrNull()
    val isTopLevel = TABS.any { it.first == top }

    fun switchTab(route: Route) {
        backStack.clear()
        backStack.add(route)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    Route.Home -> NavEntry(key) {
                        HomeScreen(
                            onEdit = { id ->
                                android.util.Log.d("AppNav", "onEdit clicked, stack=${backStack.size}")
                                backStack.add(Route.EditBaby(id))
                            },
                            onFeeding = { backStack.add(Route.Feeding) },
                            onCrying = { backStack.add(Route.Crying) },
                            onGrowth = { backStack.add(Route.GrowthForm) },
                            onAddBaby = { backStack.add(Route.EditBaby(null)) },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.Growth -> NavEntry(key) {
                        GrowthScreen(
                            onAddNode = { backStack.add(Route.NodeForm(null)) },
                            onEditNode = { id -> backStack.add(Route.NodeForm(id)) }
                        )
                    }
                    Route.Album -> NavEntry(key) {
                        AlbumScreen(onUpgrade = { backStack.add(Route.Purchase) })
                    }
                    Route.Stats -> NavEntry(key) {
                        StatsScreen(
                            onOpenChart = { backStack.add(Route.GrowthChart) },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.Mine -> NavEntry(key) {
                        MineScreen(
                            onOpenTodo = { backStack.add(Route.Todo) },
                            onOpenBackup = { backStack.add(Route.Backup) },
                            onOpenMedical = { backStack.add(Route.Medical) },
                            onOpenFeedingZone = { backStack.add(Route.FeedingZone) },
                            onOpenCryingZone = { backStack.add(Route.CryingZone) },
                            onOpenSchool = { backStack.add(Route.School) },
                            onOpenGoods = { backStack.add(Route.Goods) },
                            onOpenAnniv = { backStack.add(Route.Anniv) },
                            onOpenSync = { backStack.add(Route.Sync) },
                            onOpenPurchase = { backStack.add(Route.Purchase) },
                            onOpenBabyManage = { backStack.add(Route.BabyManage) },
                            onOpenReminderSettings = { backStack.add(Route.ReminderSettings) },
                            onOpenPrivacy = { backStack.add(Route.Privacy) },
                            onOpenAbout = { backStack.add(Route.About) }
                        )
                    }
                    is Route.EditBaby -> NavEntry(key) {
                        BabyEditScreen(babyId = key.babyId, onBack = { backStack.removeLastOrNull() })
                    }
                    Route.Feeding -> NavEntry(key) { FeedingFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Crying -> NavEntry(key) { CryingFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.GrowthForm -> NavEntry(key) { GrowthFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Todo -> NavEntry(key) { TodoScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Backup -> NavEntry(key) { BackupScreen(onBack = { backStack.removeLastOrNull() }) }
                    is Route.NodeForm -> NavEntry(key) {
                        NodeFormScreen(nodeId = key.nodeId, onBack = { backStack.removeLastOrNull() })
                    }
                    Route.GrowthChart -> NavEntry(key) {
                        GrowthChartScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.Medical -> NavEntry(key) {
                        MedicalScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onAdd = { backStack.add(Route.MedicalForm) },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.MedicalForm -> NavEntry(key) { MedicalFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.FeedingZone -> NavEntry(key) {
                        FeedingZoneScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onAdd = { backStack.add(Route.Feeding) }
                        )
                    }
                    Route.CryingZone -> NavEntry(key) {
                        CryingZoneScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onAdd = { backStack.add(Route.Crying) }
                        )
                    }
                    Route.School -> NavEntry(key) {
                        SchoolScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onAdd = { backStack.add(Route.SchoolForm) },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.SchoolForm -> NavEntry(key) { SchoolFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Goods -> NavEntry(key) {
                        GoodsScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onAdd = { backStack.add(Route.GoodsForm) },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.GoodsForm -> NavEntry(key) { GoodsFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Anniv -> NavEntry(key) {
                        AnnivScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.Sync -> NavEntry(key) {
                        SyncScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.Purchase -> NavEntry(key) { PurchaseScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.BabyManage -> NavEntry(key) {
                        BabyManageScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onEdit = { id -> backStack.add(Route.EditBaby(id)) },
                            onDetail = { id -> backStack.add(Route.BabyDetail(id)) },
                            onAdd = { backStack.add(Route.EditBaby(null)) },
                            onUpgrade = { backStack.add(Route.Purchase) }
                        )
                    }
                    Route.About -> NavEntry(key) { AboutScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Privacy -> NavEntry(key) { PrivacyPolicyScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.ReminderSettings -> NavEntry(key) { ReminderSettingsScreen(onBack = { backStack.removeLastOrNull() }) }
                    is Route.BabyDetail -> NavEntry(key) {
                        BabyDetailScreen(babyId = key.babyId, onBack = { backStack.removeLastOrNull() })
                    }
                    else -> NavEntry(key) { Text("未实现页面") }
                }
            }
        )

        if (isTopLevel) {
            BottomTabBar(
                current = top as? Route,
                onSelect = ::switchTab,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Bg)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun BottomTabBar(current: Route?, onSelect: (Route) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TABS.forEach { (route, label) ->
            val active = route == current
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Normal,
                color = if (active) Mint else TextSecondary,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(route) }
                    .padding(vertical = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
