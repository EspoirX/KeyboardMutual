package com.lzx.keyboardmutual

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.lzx.library.KeyboardLayout
import com.lzx.library.lifecycleOwner

class ChatInputView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val keyboardLayout by lazy {
        (parent as ViewGroup).findViewById<KeyboardLayout>(R.id.keyboardLayout)
    }
    private val voiceLayout by lazy {
        (parent as ViewGroup).findViewById<TextView>(R.id.voiceView)
    }
    private val emojiLayout by lazy {
        (parent as ViewGroup).findViewById<TextView>(R.id.emojiView)
    }
    private val inputView by lazy { findViewById<EditText>(R.id.editView) }
    private val emojiIcon by lazy { findViewById<ImageView>(R.id.btnFace) }
    private val voiceIcon by lazy { findViewById<ImageView>(R.id.btnVoice) }

    private val keyboardMutual by lazy { keyboardLayout.keyboardMutual }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_chat_input_bar, this, true)
    }

    fun showKeyboard() {
        if (!keyboardMutual.isKeyboardShow()) {
            keyboardMutual.forceShowKeyboard(inputView)
        }
    }

    fun hideKeyboard() {
        voiceIcon?.isSelected = false
        emojiIcon?.isSelected = false
        if (keyboardMutual.isKeyboardShow()) {
            keyboardMutual.hideKeyboard(inputView)
        } else {
            keyboardLayout.close()
        }
    }

    private fun isVoiceSelected(): Boolean = voiceIcon?.isSelected == true

    private fun isEmojiSelected(): Boolean = emojiIcon?.isSelected == true

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) return
        lifecycleOwner?.let { it ->
            keyboardMutual.visibleLiveData().observe(it, Observer {
                if (!it && !isVoiceSelected() && !isEmojiSelected()) {
                    keyboardLayout?.close()
                }
            })
        }

        inputView?.setOnClickListener {
            if (isVoiceSelected()) {
                voiceIcon?.isSelected = false
            }
            if (isEmojiSelected()) {
                emojiIcon?.isSelected = false
            }
            if (!keyboardMutual.isKeyboardShow()) {
                voiceLayout?.isVisible = false
                emojiLayout?.isVisible = false
                keyboardMutual.showKeyboard(inputView)
            }
        }

        emojiIcon?.setOnClickListener {
            voiceLayout?.isVisible = false
            voiceIcon?.isSelected = false
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                if (keyboardMutual.isKeyboardShow()) {
                    keyboardMutual.hideKeyboard(inputView)
                    postDelayed({
                        emojiLayout?.isVisible = true
                        keyboardLayout?.open()
                    }, 200)
                } else {
                    emojiLayout?.isVisible = true
                    keyboardLayout?.open()
                }
            } else {
                keyboardLayout?.close()
                emojiLayout?.isVisible = false
            }
        }

        voiceIcon?.setOnClickListener {
            emojiLayout?.isVisible = false
            emojiIcon?.isSelected = false
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                if (keyboardMutual.isKeyboardShow()) {
                    keyboardMutual.hideKeyboard(inputView)
                    postDelayed({
                        voiceLayout?.isVisible = true
                        keyboardLayout?.open()
                    }, 200)
                } else {
                    voiceLayout?.isVisible = true
                    keyboardLayout?.open()
                }
            } else {
                keyboardLayout?.close()
                voiceLayout?.isVisible = false
            }
        }

    }
}