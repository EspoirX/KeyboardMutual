package com.lzx.library

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.lzx.library.KeyboardSp.getLastHeight
import com.lzx.library.KeyboardSp.saveLastHeight

class KeyboardDialog(context: Context) : Dialog(context) {
    val visibleLiveData = MutableLiveData<Boolean>()
    val heightLiveData = MutableLiveData<Int>()

    private val rootView = FrameLayout(context).apply { setBackgroundColor(Color.TRANSPARENT) }
    private val rect = Rect()

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        setContentView(rootView)
    }

    override fun show() {
        super.show()
        window?.let {
            val lp = it.attributes.apply {
                width = 0
                height = ViewGroup.LayoutParams.MATCH_PARENT
                gravity = Gravity.TOP or Gravity.LEFT
                flags =
                    FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE or FLAG_ALT_FOCUSABLE_IM or FLAG_LAYOUT_IN_SCREEN
            }
            it.attributes = lp
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setDimAmount(0f)
            WindowCompat.setDecorFitsSystemWindows(it, true)
        }
        window?.setWindowKeyboardListener(rootView, rect) { imeHeight, imeVisible ->
            if (imeHeight > 0 && getLastHeight() != imeHeight) {
                saveLastHeight(imeHeight)
            }
            visibleLiveData.value = imeVisible
            heightLiveData.value = imeHeight
        }
    }

    fun checkKeyboardState(): Boolean {
        val window = window ?: return false
        val insets = ViewCompat.getRootWindowInsets(window.decorView)
        return insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    }
}