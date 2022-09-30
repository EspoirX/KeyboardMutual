package com.lzx.library

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Observer

class KeyboardLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    val keyboardMutual by lazy { KeyboardMutual() }
    private var openAnim: ValueAnimator? = null
    private var closeAnim: ValueAnimator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) return
        lifecycleOwner?.let { it ->
            keyboardMutual.heightLiveData().observe(it, Observer {
                if (it > 0) {
                    openAnim?.cancel()
                    closeAnim?.cancel()
                    realAnimHeight(it.toFloat())
                }
            })
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isInEditMode) return
        openAnim?.cancel()
        closeAnim?.cancel()
        openAnim = null
        closeAnim = null
    }

    fun isOpen(): Boolean {
        return height > 0
    }

    fun open() {
        if (isOpen()) {
            return
        }
        openAnim?.cancel()
        closeAnim?.cancel()
        val imeHeight = keyboardMutual.height()
        openAnim = if (imeHeight == 0) {
            realAnimHeight(minimumHeight.toFloat())
        } else {
            realAnimHeight(imeHeight.toFloat())
        }
    }

    fun close() {
        if (!isOpen()) {
            return
        }
        openAnim?.cancel()
        closeAnim?.cancel()
        closeAnim = realAnimHeight(0f)
    }

    fun openDirect() {
        val imeHeight = keyboardMutual.height()
        this.layoutParams.height = if (imeHeight == 0) minimumHeight else imeHeight
        this.requestLayout()
    }

    fun closeDirect() {
        this.layoutParams.height = 0
        this.requestLayout()
    }

    private fun realAnimHeight(to: Float, duration: Long = 100) = animHeight(to = to, duration = duration)
}