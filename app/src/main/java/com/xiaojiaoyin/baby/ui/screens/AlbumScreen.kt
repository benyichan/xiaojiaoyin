package com.xiaojiaoyin.baby.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun AlbumScreen() {
    Box(modifier = Modifier.fillMaxSize().statusBarsPadding(), contentAlignment = Alignment.Center) {
        Text("相册（M2）", fontSize = 16.sp)
    }
}
