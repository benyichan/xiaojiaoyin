package com.xiaojiaoyin.baby.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.ui.theme.Blue
import com.xiaojiaoyin.baby.ui.theme.BlueLight
import com.xiaojiaoyin.baby.ui.theme.Gold
import com.xiaojiaoyin.baby.ui.theme.GoldLight
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.MintLight
import com.xiaojiaoyin.baby.ui.theme.Pink
import com.xiaojiaoyin.baby.ui.theme.PinkLight

enum class TagKind {
    FEEDING, CRYING, PHOTO, GROWTH, NODE
}

@Composable
fun TypeTag(kind: TagKind, text: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (kind) {
        TagKind.FEEDING -> MintLight to Mint
        TagKind.CRYING -> PinkLight to Pink
        TagKind.PHOTO -> GoldLight to Gold
        TagKind.GROWTH -> BlueLight to Blue
        TagKind.NODE -> GoldLight to Gold
    }
    Text(
        text = text,
        modifier = modifier
            .background(bg, RoundedCornerShape(9.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        color = fg,
        fontSize = 10.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
}
