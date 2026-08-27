package com.xiaojiaoyin.baby.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Checkbox
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
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
import com.xiaojiaoyin.baby.data.AppGraph
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import com.xiaojiaoyin.baby.reminder.ReminderPermissionHelper
import com.xiaojiaoyin.baby.reminder.ReminderScheduler
import com.xiaojiaoyin.baby.ui.components.FormField
import com.xiaojiaoyin.baby.ui.components.OverlayHeader
import com.xiaojiaoyin.baby.ui.components.TextInputField
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import com.xiaojiaoyin.baby.ui.theme.Red
import com.xiaojiaoyin.baby.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val babyId by com.xiaojiaoyin.baby.ui.common.rememberCurrentBabyId()
    val todos by remember(babyId) {
        if (babyId == null) kotlinx.coroutines.flow.flowOf(emptyList<TodoEntity>())
        else AppGraph.todoRepository.observeAll(babyId!!)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val scheduler = remember { ReminderScheduler(context.applicationContext) }
    var showAdd by remember { mutableStateOf(false) }
    var showPermissionGuide by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoEntity?>(null) }
    var pendingDelete by remember { mutableStateOf<TodoEntity?>(null) }

    fun toggleRemind(todo: TodoEntity, enabled: Boolean) {
        scope.launch {
            val updated = todo.copy(remindEnabled = enabled)
            AppGraph.todoRepository.update(updated)
            if (enabled) {
                ReminderPermissionHelper.requestNotificationPermission(context as? ComponentActivity ?: return@launch)
                scheduler.schedule(updated)
                if (!ReminderPermissionHelper.canScheduleExact(context)) {
                    showPermissionGuide = true
                }
            } else {
                scheduler.cancel(todo.id)
            }
        }
    }

    fun toggleDone(todo: TodoEntity) {
        scope.launch {
            AppGraph.todoRepository.update(todo.copy(completed = !todo.completed))
            if (!todo.completed) scheduler.cancel(todo.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader("待办清单", onBack)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable { showAdd = true }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("＋ 新建待办", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (todos.isEmpty()) {
            Text(
                "还没有待办，点上面新建一个",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            todos.forEach { todo ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            pendingDelete = todo
                            false
                        } else false
                    }
                )
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = { Box(Modifier.fillMaxSize()) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                            .background(Card, RoundedCornerShape(18.dp))
                            .clickable { editingTodo = todo }
                            .padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = todo.completed,
                            onCheckedChange = { toggleDone(todo) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = todo.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todo.completed) TextSecondary else TextPrimary,
                                textDecoration = if (todo.completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                            Text(
                                text = formatTodoTime(todo.timeAt),
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                        Switch(
                            checked = todo.remindEnabled,
                            onCheckedChange = { toggleRemind(todo, it) }
                        )
                    }
                }
            }
        }
    }

    if (showAdd || editingTodo != null) {
        AddTodoForm(
            todo = editingTodo,
            onBack = { showAdd = false },
            onSave = { title, timeAt, remind ->
                scope.launch {
                    if (editingTodo != null) {
                        val updated = editingTodo!!.copy(
                            title = title,
                            timeAt = timeAt,
                            remindEnabled = remind
                        )
                        AppGraph.todoRepository.update(updated)
                        scheduler.cancel(updated.id)
                        if (remind) scheduler.schedule(updated)
                    } else {
                        val id = AppGraph.todoRepository.add(
                            babyId = babyId ?: 0L,
                            title = title,
                            timeAt = timeAt,
                            remindEnabled = remind
                        )
                        if (remind) {
                            scheduler.schedule(
                                TodoEntity(
                                    id = id,
                                    babyId = babyId ?: 0L,
                                    title = title,
                                    timeAt = timeAt,
                                    remindEnabled = true,
                                    createdAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    showAdd = false
                    editingTodo = null
                }
            }
        )
    }

    if (showPermissionGuide) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPermissionGuide = false },
            title = { Text("开启精确提醒") },
            text = {
                Text(
                    "系统默认关闭了精确闹钟权限，到点提醒可能不触发。\n\n" +
                        ReminderPermissionHelper.vendorHint(context)?.let { "$it\n\n" } ?: "" +
                        "请在系统设置中允许「闹钟和提醒」权限。"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionGuide = false
                    ReminderPermissionHelper.openExactAlarmSettings(context)
                }) { Text("去开启") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionGuide = false }) { Text("稍后") }
            }
        )
    }

    pendingDelete?.let { target ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除待办", fontWeight = FontWeight.Bold) },
            text = { Text("确定删除「${target.title}」？删除后提醒也会取消。") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        AppGraph.todoRepository.delete(target)
                        scheduler.cancel(target.id)
                    }
                    pendingDelete = null
                }) { Text("删除", color = Red) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("取消") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTodoForm(
    todo: TodoEntity?,
    onBack: () -> Unit,
    onSave: (title: String, timeAt: Long, remind: Boolean) -> Unit
) {
    var title by remember(todo) { mutableStateOf(todo?.title ?: "") }
    var timeAt by remember(todo) {
        mutableStateOf(todo?.timeAt ?: System.currentTimeMillis())
    }
    var remind by remember(todo) { mutableStateOf(todo?.remindEnabled ?: true) }
    var showTime by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val zone = ZoneId.of("Asia/Shanghai")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.xiaojiaoyin.baby.ui.theme.Bg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp)
    ) {
        OverlayHeader(if (todo == null) "新建待办" else "编辑待办", onBack)
        TextInputField(
            label = "标题 *",
            value = title,
            onValueChange = { title = it },
            placeholder = "如 打疫苗、吃 AD 滴剂"
        )
        FormField(
            label = "提醒时间",
            value = Instant.ofEpochMilli(timeAt).atZone(zone)
                .format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
            onClick = { showTime = true }
        )
        FormField(
            label = "提醒日期",
            value = Instant.ofEpochMilli(timeAt).atZone(zone)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            onClick = { showDate = true }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .background(Card, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("到点提醒", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Switch(checked = remind, onCheckedChange = { remind = it })
        }
        error?.let {
            Text(it, fontSize = 12.sp, color = Red, modifier = Modifier.padding(horizontal = 16.dp))
        }
        SaveButton("保存") {
            if (title.isBlank()) {
                error = "请填写标题"
                return@SaveButton
            }
            if (timeAt <= System.currentTimeMillis()) {
                error = "提醒时间已过，请选择未来的时间"
                return@SaveButton
            }
            onSave(title.trim(), timeAt, remind)
        }
    }

    if (showTime) {
        val t = Instant.ofEpochMilli(timeAt).atZone(zone)
        val timeState = rememberTimePickerState(initialHour = t.hour, initialMinute = t.minute)
        TimePickerDialog(
            onDismissRequest = { showTime = false },
            title = { Text("选择时间") },
            confirmButton = {
                TextButton(onClick = {
                    timeAt = t.toLocalDate().atTime(timeState.hour, timeState.minute)
                        .atZone(zone).toInstant().toEpochMilli()
                    showTime = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text("取消") } }
        ) {
            TimePicker(state = timeState)
        }
    }

    if (showDate) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = timeAt)
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { selected ->
                        val date = Instant.ofEpochMilli(selected).atZone(zone).toLocalDate()
                        val old = Instant.ofEpochMilli(timeAt).atZone(zone)
                        timeAt = date.atTime(old.hour, old.minute).atZone(zone).toInstant().toEpochMilli()
                    }
                    showDate = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text("取消") } }
        ) {
            DatePicker(state = dateState)
        }
    }
}

private fun formatTodoTime(millis: Long): String {
    val t = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Shanghai"))
    return "${t.year}-${t.monthValue.toString().padStart(2, '0')}-${t.dayOfMonth.toString().padStart(2, '0')} ${String.format("%02d:%02d", t.hour, t.minute)}"
}
