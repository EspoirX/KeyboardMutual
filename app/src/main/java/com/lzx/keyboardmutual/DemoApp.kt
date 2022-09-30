package com.lzx.keyboardmutual

import android.app.Application
import com.lzx.library.KeyboardMutual

class DemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KeyboardMutual.init(this)
    }
}