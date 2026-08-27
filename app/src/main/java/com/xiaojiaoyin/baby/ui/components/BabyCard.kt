package com.xiaojiaoyin.baby.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintGradientEnd
import com.xiaojiaoyin.baby.ui.theme.MintGradientStart

@Composable
fun BabyCard(
    avatarText: String,
    avatarPath: String,
    name: String,
    genderBadge: String,
    subText: String,
    onSwitch: () -> Unit
) {
    val context = LocalContext.current
    val avatarBmp = if (avatarPath.isNotEmpty()) {
        remember(avatarPath) {
            com.xiaojiaoyin.baby.data.PhotoStorage.decodeThumb(
                com.xiaojiaoyin.baby.data.PhotoStorage.loadFile(context, avatarPath),
                160
            )
        }
    } else null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                Brush.linearGradient(listOf(MintGradientStart, MintGradientEnd)),
                androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (avatarBmp != null) {
            Image(
                bitmap = avatarBmp.asImageBitmap(),
                contentDescription = "宝宝头像",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f), CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.92f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarText,
                    color = Mint,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = name, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = genderBadge,
                    color = Mint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                        .padding(horizontal = 9.dp, vertical = 2.dp)
                )
            }
            Text(
                text = subText,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        Text(
            text = "切换 ›",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            modifier = Modifier.clickable(onClick = onSwitch)
        )
    }
}
