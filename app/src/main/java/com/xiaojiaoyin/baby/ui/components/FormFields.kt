package com.xiaojiaoyin.baby.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojiaoyin.baby.ui.theme.Card
import com.xiaojiaoyin.baby.ui.theme.Mint
import com.xiaojiaoyin.baby.ui.theme.TextPrimary
import com.xiaojiaoyin.baby.ui.theme.TextSecondary

@Composable
fun FormField(
    label: String,
    value: String,
    onClick: () -> Unit,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp)
                .background(Card, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(13.dp)
        )
    }
}

@Composable
fun SegmentedField(
    label: String,
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp)) {
            options.forEachIndexed { index, option ->
                Text(
                    text = option,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (index == selected) androidx.compose.ui.graphics.Color.White else TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (index == selected) Mint else Card,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelect(index) }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun TextInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "点击输入"
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 14.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = TextSecondary) },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Mint,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedContainerColor = Card,
                unfocusedContainerColor = Card
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp)
        )
    }
}
