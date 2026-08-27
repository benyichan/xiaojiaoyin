package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.VaccinationEntity
import com.xiaojiaoyin.baby.domain.VaccineDose
import com.xiaojiaoyin.baby.domain.VaccineSchedule
import com.xiaojiaoyin.baby.domain.VaccineStatus
import com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.Red
import com.xiaojiaoyin.baby.ui.theme.RedLight
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

private data class VaccineRow(val dose: VaccineDose, val vaccinated: VaccinationEntity?, val status: VaccineStatus)

@Composable
fun VaccineScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val babyId by rememberCurrentBabyId()
    var baby by remember { mutableStateOf<com.xiaojiaoyin.baby.data.db.entity.BabyEntity?>(null) }
    LaunchedEffect(babyId) {
        baby = babyId?.let { AppGraph.babyRepository.getById(it) }
    }
    val doses = remember { VaccineSchedule.load(context) }
    val vaccinatedList by remember(babyId) {
        if (babyId == null) kotlinx.coroutines.flow.flowOf(emptyList<VaccinationEntity>())
        else AppGraph.vaccinationRepository.observeByBaby(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val vaccinatedByKey = remember(vaccinatedList) { vaccinatedList.associateBy { it.doseKey } }
    var confirmDose by remember { mutableStateOf<VaccineRow?>(null) }
    var pendingUnmark by remember { mutableStateOf<VaccineRow?>(null) }

    val rows = remember(doses, vaccinatedByKey, baby) {
        val now = System.currentTimeMillis()
        val birth = baby?.birthDateTime ?: now
        doses.map { dose ->
            val v = vaccinatedByKey[dose.key]
            VaccineRow(dose, v, VaccineSchedule.statusOf(dose, v, birth, now))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("疫苗接种", onBack)
        val doneCount = rows.count { it.status == VaccineStatus.DONE }
        Text(
            "国家免疫规划（2021 版）共 ${rows.size} 剂 · 已种 $doneCount 剂",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows.size) { idx ->
                val row = rows[idx]
                val (bg, fg, statusText) = when (row.status) {
                    VaccineStatus.DONE -> Triple(MintLight, Mint, "已种")
                    VaccineStatus.OVERDUE -> Triple(RedLight, Red, "逾期未种")
                    VaccineStatus.DUE_SOON -> Triple(GoldLight, Gold, "即将到期")
                    VaccineStatus.UPCOMING -> Triple(Card, TextSecondary, "未到龄")
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Card, RoundedCornerShape(16.dp))
                        .clickable {
                            if (row.status == VaccineStatus.DONE) pendingUnmark = row
                            else confirmDose = row
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${row.dose.vaccine} ${row.dose.doseLabel}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = fg,
                            modifier = Modifier
                                .background(bg, RoundedCornerShape(50.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    val dueText = baby?.let {
                        "建议 ${VaccineSchedule.dueDateText(it.birthDateTime, row.dose)}（${row.dose.ageLabel}）"
                    } ?: ""
                    val vaccinatedText = row.vaccinated?.let { v ->
                        if (v.vaccinatedAt > 0) " · 接种于 " + DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            .format(Instant.ofEpochMilli(v.vaccinatedAt).atZone(ZoneId.of("Asia/Shanghai")))
                        else " · 已标记"
                    } ?: ""
                    Text(
                        "$dueText$vaccinatedText · 点击${if (row.status == VaccineStatus.DONE) "取消标记" else "标记已种"}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (row.vaccinated?.note?.isNotBlank() == true) {
                        Text(row.vaccinated.note, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }

    confirmDose?.let { row ->
        AlertDialog(
            onDismissRequest = { confirmDose = null },
            title = { Text("标记已接种", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("「${row.dose.vaccine} ${row.dose.doseLabel}」已经接种了吗？") },
            confirmButton = {
                TextButton(onClick = {
                    val bid = babyId ?: return@TextButton
                    scope.launch {
                        AppGraph.vaccinationRepository.markVaccinated(
                            bid, row.dose.key, System.currentTimeMillis(), ""
                        )
                        baby?.let {
                            runCatching {
                                com.xiaojiaoyin.baby.reminder.VaccineScheduler(context).scheduleForBaby(it)
                            }
                        }
                        confirmDose = null
                    }
                }) { Text("已接种", color = Mint) }
            },
            dismissButton = { TextButton(onClick = { confirmDose = null }) { Text("取消") } }
        )
    }
    pendingUnmark?.let { row ->
        AlertDialog(
            onDismissRequest = { pendingUnmark = null },
            title = { Text("取消已种标记", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = { Text("把「${row.dose.vaccine} ${row.dose.doseLabel}」改回未接种？") },
            confirmButton = {
                TextButton(onClick = {
                    val v = row.vaccinated ?: return@TextButton
                    scope.launch {
                        AppGraph.vaccinationRepository.unmark(v)
                        pendingUnmark = null
                    }
                }) { Text("改回未种", color = Red) }
            },
            dismissButton = { TextButton(onClick = { pendingUnmark = null }) { Text("取消") } }
        )
    }
}
