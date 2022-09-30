package com.lzx.library

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner

val View.lifecycleOwner: LifecycleOwner?
    get() {
        return ViewTreeLifecycleOwner.get(this)
    }


fun View.animHeight(from: Float = layoutParams.height.toFloat(),
                    to: Float,
                    duration: Long = 300,
                    interpolator: Interpolator = LinearInterpolator()): ValueAnimator {
    return ValueAnimator.ofFloat(from, to).apply {
        this.duration = duration
        this.interpolator = interpolator

        addUpdateListener { animation ->
            val value = animation?.animatedValue as Float?
            value?.let {
                this@animHeight.layoutParams.height = it.toInt()
                this@animHeight.requestLayout()
            }
        }
        start()
    }
}