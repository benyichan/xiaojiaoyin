package com.xiaojiaoyin.baby

import android.app.Application
import com.xiaojiaoyin.baby.data.AppGraph

class BabyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
