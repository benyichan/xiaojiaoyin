package com.xiaojiaoyin.baby.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.PhotoStorage
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordType
import com.xiaojiaoyin.baby.ui.components.BabyCard
import com.xiaojiaoyin.baby.ui.components.SectionHeader
import com.xiaojiaoyin.baby.ui.components.TagKind
import com.xiaojiaoyin.baby.ui.components.TypeTag
import com.xiaojiaoyin.baby.ui.theme.Blue
import com.xiaojiaoyin.baby.ui.theme.BlueLight
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.Pink
import com.xiaojiaoyin.baby.ui.theme.PinkLight
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import com.xiaojiaoyin.baby.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEdit: () -> Unit = {},
    onFeeding: () -> Unit = {},
    onCrying: () -> Unit = {},
    onGrowth: () -> Unit = {}
) {
    val vm: HomeViewModel = viewModel {
        HomeViewModel(AppGraph.babyRepository, AppGraph.recordRepository, AppGraph.settingsRepository)
    }
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showBabyPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val path = PhotoStorage.saveImage(context, uri)
                val babyId = AppGraph.settingsRepository.resolveCurrentBabyId(AppGraph.babyRepository)
                if (path != null && babyId != null) {
                    AppGraph.recordRepository.add(
                        babyId = babyId,
                        type = RecordType.PHOTO,
                        occurredAt = System.currentTimeMillis(),
                        detailJson = JSONObject().put("path", path).toString()
                    )
                }
            }
        }
    }

    if (showBabyPicker && state.babies.size > 1) {
        ModalBottomSheet(onDismissRequest = { showBabyPicker = false }) {
            state.babies.forEach { baby ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                AppGraph.settingsRepository.setCurrentBaby(baby.id)
                            }
                            showBabyPicker = false
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MintLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(baby.name.take(1), color = Mint, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${baby.name} · ${baby.nickname}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    if (baby.id == state.currentBaby?.id) {
                        Text(
                            text = "当前",
                            fontSize = 11.sp,
                            color = Mint,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(MintLight, RoundedCornerShape(9.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }

    if (state.currentBaby == null) {
        EmptyHome(onAdd = onEdit)
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottom = 70.dp)
    ) {
        item { Spacer(Modifier.height(12.dp)) }
        item {
            val baby = state.currentBaby!!
            BabyCard(
                avatarText = baby.name.take(1),
                name = baby.name,
                genderBadge = if (baby.gender == "男") "男宝" else "女宝",
                subText = "小名 ${baby.nickname} · ${state.ageText}",
                onSwitch = { showBabyPicker = true }
            )
        }
        item { InfoCard(baby = state.currentBaby!!, derivedText = state.derived, onEdit = onEdit) }
        item { BirthdayCards(derived = state.derived) }
        item {
            QuickActions(
                onFeeding = onFeeding,
                onCrying = onCrying,
                onGrowth = onGrowth,
                onPhoto = { photoPicker.launch(arrayOf("*/*")) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item {
            SectionHeader(
                title = "最近动态",
                moreText = null,
                onMoreClick = null
            )
        }
        if (state.feedRecords.isEmpty()) {
            item {
                Text(
                    text = "还没有记录，点上面的按钮记第一条吧",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
            }
        } else {
            items(state.feedRecords.size) { index ->
                FeedCard(record = state.feedRecords[index])
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun EmptyHome(onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("还没有宝宝档案", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            "添加第一个宝宝，开始记录成长",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = onAdd,
            modifier = Modifier.padding(top = 20.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("添加宝宝", fontSize = 15.sp)
        }
    }
}

@Composable
private fun InfoCard(baby: BabyEntity, derivedText: com.xiaojiaoyin.baby.domain.DerivedInfo?, onEdit: () -> Unit) {
    val d = derivedText
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(Card, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "编辑 ›",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Mint,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onEdit),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        if (d == null) {
            Text("计算中…", fontSize = 14.sp, color = TextSecondary)
            return@Column
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            InfoCell("出生", formatBirth(baby.birthDateTime), Modifier.weight(1f))
            InfoCell("生肖", d.zodiac, Modifier.weight(1f))
            InfoCell("星座", d.constellation, Modifier.weight(1f))
            InfoCell("农历", d.lunarDate, Modifier.weight(1f))
        }
        Text(
            text = "八字 · 天干地支",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = d.baZi,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Mint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .background(MintLight, RoundedCornerShape(12.dp))
                .padding(10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun InfoCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

private fun formatBirth(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}-${t.monthValue.toString().padStart(2, '0')}-${t.dayOfMonth.toString().padStart(2, '0')} ${String.format("%02d:%02d", t.hour, t.minute)}"
}

@Composable
private fun BirthdayCards(derived: com.xiaojiaoyin.baby.domain.DerivedInfo?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        BirthdayCard(
            label = "阳历生日",
            days = derived?.nextSolarBirthday?.days ?: 0,
            date = derived?.nextSolarBirthday?.dateText ?: "",
            color = Gold,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(10.dp))
        BirthdayCard(
            label = "农历生日",
            days = derived?.nextLunarBirthday?.days ?: 0,
            date = derived?.nextLunarBirthday?.dateText ?: "",
            color = Pink,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BirthdayCard(
    label: String,
    days: Long,
    date: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Card, RoundedCornerShape(18.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Text(
            text = "$days 天",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(date, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun QuickActions(
    onFeeding: () -> Unit,
    onCrying: () -> Unit,
    onGrowth: () -> Unit,
    onPhoto: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickItem("喂养", Mint, onFeeding, Modifier.weight(1f))
        QuickItem("哭闹", Pink, onCrying, Modifier.weight(1f))
        QuickItem("生长", Blue, onGrowth, Modifier.weight(1f))
        QuickItem("拍照", Gold, onPhoto, Modifier.weight(1f))
    }
}

@Composable
private fun QuickItem(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Card, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun FeedCard(record: com.xiaojiaoyin.baby.data.db.entity.RecordEntity) {
    val ui = when (record.type) {
        RecordType.FEEDING -> {
            val detail = runCatching { org.json.JSONObject(record.detailJson) }.getOrNull()
            val kindText = detail?.optString("kind", "母乳") ?: "母乳"
            val amount = detail?.optString("amount", "") ?: ""
            FeedUi(
                kind = TagKind.FEEDING,
                tagText = "喂养",
                title = if (kindText == "母乳") "母乳 · $amount" else "$kindText · $amount",
                sub = formatTime(record.occurredAt)
            )
        }
        RecordType.CRYING -> {
            val detail = runCatching { org.json.JSONObject(record.detailJson) }.getOrNull()
            val reason = detail?.optString("reason", "哭闹") ?: "哭闹"
            FeedUi(
                kind = TagKind.CRYING,
                tagText = "哭闹",
                title = reason,
                sub = formatTime(record.occurredAt)
            )
        }
        RecordType.GROWTH -> {
            val detail = runCatching { org.json.JSONObject(record.detailJson) }.getOrNull()
            val h = detail?.optDouble("heightCm", 0.0) ?: 0.0
            val w = detail?.optDouble("weightKg", 0.0) ?: 0.0
            FeedUi(
                kind = TagKind.GROWTH,
                tagText = "生长",
                title = "身高 ${h}cm · 体重 ${w}kg",
                sub = formatTime(record.occurredAt)
            )
        }
        RecordType.PHOTO -> {
            FeedUi(
                kind = TagKind.PHOTO,
                tagText = "相册",
                title = record.note.ifBlank { "新照片" },
                sub = formatTime(record.occurredAt)
            )
        }
        RecordType.NODE -> {
            FeedUi(
                kind = TagKind.NODE,
                tagText = "节点",
                title = record.title(),
                sub = formatTime(record.occurredAt)
            )
        }
        RecordType.MEDICAL -> {
            val detail = runCatching { org.json.JSONObject(record.detailJson) }.getOrNull()
            val category = detail?.optString("category", "医疗") ?: "医疗"
            val title = detail?.optString("title", "医疗记录") ?: "医疗记录"
            FeedUi(
                kind = TagKind.NODE,
                tagText = category,
                title = title,
                sub = formatTime(record.occurredAt)
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .background(Card, RoundedCornerShape(18.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TypeTag(ui.kind, ui.tagText)
        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp)) {
            Text(ui.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(ui.sub, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private data class FeedUi(
    val kind: TagKind,
    val tagText: String,
    val title: String,
    val sub: String
)

private fun formatTime(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.monthValue}月${t.dayOfMonth}日 ${String.format("%02d:%02d", t.hour, t.minute)}"
}
