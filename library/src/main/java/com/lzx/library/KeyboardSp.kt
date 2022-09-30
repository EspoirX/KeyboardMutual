package com.lzx.library

import android.content.Context

object KeyboardSp {
    const val KEY_SP_FILE = "com.lzx.keyboard.preference"
    const val KEY_HEIGHT = "last_keyboard_height"

    private val sharedPreferences by lazy {
        KeyboardActivityManager.INSTANCE.currentActivity?.getSharedPreferences(KEY_SP_FILE,
            Context.MODE_PRIVATE)
    }

    fun saveLastHeight(height: Int) {
        sharedPreferences?.edit()?.let {
            it.putInt(KEY_HEIGHT, height)
            it.apply()
        }
    }

    fun getLastHeight(): Int {
        return sharedPreferences?.getInt(KEY_HEIGHT, 0) ?: 0
    }
}