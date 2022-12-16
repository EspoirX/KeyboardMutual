package com.lzx.library

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer

class KeyboardLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), LifecycleOwner {

    val keyboardMutual by lazy { KeyboardMutual() }
    private var openAnim: ValueAnimator? = null
    private var closeAnim: ValueAnimator? = null
    private var mLifecycleRegistry: LifecycleRegistry? = null

    init {
        mLifecycleRegistry = LifecycleRegistry(this)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.KeyboardLayout)
        val observerKeyboardForMySelf =
            ta.getBoolean(R.styleable.KeyboardLayout_observerKeyboardForMySelf, false)
        if (observerKeyboardForMySelf) {
            keyboardMutual.observerKeyboardForMySelf()
        }
        ta.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mLifecycleRegistry?.currentState = Lifecycle.State.CREATED
        if (isInEditMode) return
        keyboardMutual.heightLiveData().observe(this, Observer {
            if (it > 0) {
                openAnim?.cancel()
                closeAnim?.cancel()
                realAnimHeight(it.toFloat())
            }
        })
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_START)
            mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            mLifecycleRegistry?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLifecycleRegistry?.currentState = Lifecycle.State.DESTROYED
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

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry!!
    }
}