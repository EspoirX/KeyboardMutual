package com.lzx.library

import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.lzx.library.KeyboardSp.getLastHeight

class KeyboardMutual {

    companion object {
        @JvmStatic
        fun init(application: Application) {
            KeyboardActivityManager.INSTANCE.init(application)
        }
    }

    private var dialog: KeyboardDialog
    private var observerKeyboardForMySelf: Boolean = false

    init {
        val current = KeyboardActivityManager.INSTANCE.currentActivity
            ?: throw IllegalStateException("No activity found! Should be called after Activity onCreate method!")
        dialog = KeyboardDialog(current)

        if (current is ComponentActivity) {
            current.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_CREATE) {
                        if (!observerKeyboardForMySelf) {
                            startObserver()
                        }
                    } else if (event == Lifecycle.Event.ON_DESTROY) {
                        if (!observerKeyboardForMySelf) {
                            stopObserver()
                        }
                    }
                }
            })
        }
    }

    /**
     * 设置自己操作开始和结束监听键盘
     */
    fun observerKeyboardForMySelf() {
        observerKeyboardForMySelf = true
    }

    /**
     * 开始监听键盘（observerMySelf为true的时候才用）
     */
    fun startObserver() {
        runCatching {
            if (!dialog.isShowing) {
                dialog.show()
            }
        }
    }

    /**
     * 结束监听键盘（observerMySelf为true的时候才用）
     */
    fun stopObserver() {
        runCatching {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    fun heightLiveData(): MutableLiveData<Int> {
        return dialog.heightLiveData
    }

    fun height(): Int {
        return getLastHeight()
    }

    fun visibleLiveData(): MutableLiveData<Boolean> {
        return dialog.visibleLiveData
    }

    fun isKeyboardShow(): Boolean {
        return dialog.checkKeyboardState()
    }

    fun showKeyboard(view: View) {
        ViewCompat.getWindowInsetsController(view)?.show(WindowInsetsCompat.Type.ime())
    }

    fun forceShowKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(view: View) {
        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())
    }

    fun forceHideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(0, 0)
    }
}