package com.xiaojiaoyin.baby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.xiaojiaoyin.baby.navigation.AppNav
import com.xiaojiaoyin.baby.ui.theme.BabyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyAppTheme {
                AppNav()
            }
        }
    }
}
