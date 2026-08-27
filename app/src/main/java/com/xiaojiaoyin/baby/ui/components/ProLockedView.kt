package com.xiaojiaoyin.baby.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextSecondary
import com.xiaojiaoyin.baby.ui.theme.TextPrimary

/** 免费版锁定页：提示升级 Pro */
@Composable
fun ProLockedView(title: String, desc: String, onUpgrade: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 60.dp)
                .background(Gold, RoundedCornerShape(20.dp))
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Text("Pro", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            desc,
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = "升级 Pro",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier
                .padding(top = 24.dp)
                .background(Mint, RoundedCornerShape(14.dp))
                .clickable(onClick = onUpgrade)
                .padding(horizontal = 40.dp, vertical = 13.dp)
        )
    }
}
