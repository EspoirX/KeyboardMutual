package com.lzx.library

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

/**
 *  键盘监听
 */
fun Window.setWindowKeyboardListener(rootView: View?,
                                     rect: Rect?,
                                     onChanged: ((imeHeight: Int, imeVisible: Boolean) -> Unit)? = null) {
    var heightMax = 0
    var wasOpened = false

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && rootView != null && rect != null) {
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        decorView.viewTreeObserver.addOnGlobalLayoutListener {
            rootView.getWindowVisibleDisplayFrame(rect)
            if (rect.bottom > heightMax) {
                heightMax = rect.bottom
            } // 两者的差值就是键盘的高度
            val imeHeight = heightMax - rect.bottom
            val phoneHeight = Resources.getSystem().displayMetrics.heightPixels
            val heightDiff = phoneHeight - rect.height()
            val imeVisible = heightDiff > phoneHeight * 0.15
            if (imeVisible == wasOpened) {
                return@addOnGlobalLayoutListener
            }
            wasOpened = imeVisible
            onChanged?.invoke(imeHeight, imeVisible)
        }
        return
    }

    // 部分系统不支持WindowInsets使用兼容方案处理
    if (!decorView.isSystemInsetsAnimationSupport()) {
        return setWindowSoftInputCompatible(onChanged)
    }
    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    var imeVisible = false
    var imeHeight = 0
    val callback = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {

        override fun onStart(animation: WindowInsetsAnimationCompat,
                             bounds: WindowInsetsAnimationCompat.BoundsCompat): WindowInsetsAnimationCompat.BoundsCompat {
            imeVisible =
                ViewCompat.getRootWindowInsets(decorView)?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
            return bounds
        }

        override fun onProgress(insets: WindowInsetsCompat,
                                runningAnimations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
            imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            return insets
        }

        override fun onEnd(animation: WindowInsetsAnimationCompat) {
            super.onEnd(animation)
            onChanged?.invoke(imeHeight, imeVisible)
        }
    }
    ViewCompat.setWindowInsetsAnimationCallback(decorView, callback)
}

/** 部分系统不支持WindowInsets使用兼容方案处理 */
private fun Window.setWindowSoftInputCompatible(onChanged: ((imeHeight: Int, imeVisible: Boolean) -> Unit)? = null) {
    setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    var shown = false
    decorView.viewTreeObserver.addOnGlobalLayoutListener {
        val rootWindowInsets = ViewCompat.getRootWindowInsets(decorView) ?: return@addOnGlobalLayoutListener
        val imeHeight = rootWindowInsets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        val imeVisible = rootWindowInsets.isVisible(WindowInsetsCompat.Type.ime())
        if (imeVisible) {
            if (!shown) {
                onChanged?.invoke(imeHeight, true)
            }
            shown = true
        } else {
            if (shown) {
                onChanged?.invoke(imeHeight, false)
            }
            shown = false
        }
    }
}

/** 判断系统是否支持[WindowInsetsAnimationCompat] */
internal fun View.isSystemInsetsAnimationSupport(): Boolean {
    val windowInsetsController = ViewCompat.getWindowInsetsController(this)
    return !(windowInsetsController == null || windowInsetsController.systemBarsBehavior == 0)
}