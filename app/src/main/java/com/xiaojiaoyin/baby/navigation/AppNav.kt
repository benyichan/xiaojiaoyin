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
import com.xiaojiaoyin.baby.ui.screens.BackupScreen
import com.xiaojiaoyin.baby.ui.screens.BabyEditScreen
import com.xiaojiaoyin.baby.ui.screens.CryingFormScreen
import com.xiaojiaoyin.baby.ui.screens.FeedingFormScreen
import com.xiaojiaoyin.baby.ui.screens.GrowthFormScreen
import com.xiaojiaoyin.baby.ui.screens.GrowthScreen
import com.xiaojiaoyin.baby.ui.screens.HomeScreen
import com.xiaojiaoyin.baby.ui.screens.MineScreen
import com.xiaojiaoyin.baby.ui.screens.NodeFormScreen
import com.xiaojiaoyin.baby.ui.screens.StatsScreen
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
    data object EditBaby : Route
    data object Feeding : Route
    data object Crying : Route
    data object GrowthForm : Route
    data object Todo : Route
    data object Backup : Route
    data object NodeForm : Route
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
                            onEdit = {
                                android.util.Log.d("AppNav", "onEdit clicked, stack=${backStack.size}")
                                backStack.add(Route.EditBaby)
                            },
                            onFeeding = { backStack.add(Route.Feeding) },
                            onCrying = { backStack.add(Route.Crying) },
                            onGrowth = { backStack.add(Route.GrowthForm) }
                        )
                    }
                    Route.Growth -> NavEntry(key) {
                        GrowthScreen(onAddNode = { backStack.add(Route.NodeForm) })
                    }
                    Route.Album -> NavEntry(key) { AlbumScreen() }
                    Route.Stats -> NavEntry(key) { StatsScreen() }
                    Route.Mine -> NavEntry(key) {
                        MineScreen(
                            onOpenTodo = { backStack.add(Route.Todo) },
                            onOpenBackup = { backStack.add(Route.Backup) }
                        )
                    }
                    Route.EditBaby -> NavEntry(key) { BabyEditScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Feeding -> NavEntry(key) { FeedingFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Crying -> NavEntry(key) { CryingFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.GrowthForm -> NavEntry(key) { GrowthFormScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Todo -> NavEntry(key) { TodoScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.Backup -> NavEntry(key) { BackupScreen(onBack = { backStack.removeLastOrNull() }) }
                    Route.NodeForm -> NavEntry(key) { NodeFormScreen(onBack = { backStack.removeLastOrNull() }) }
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
