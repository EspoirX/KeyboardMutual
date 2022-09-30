package com.lzx.keyboardmutual

import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        findViewById<TextView>(R.id.content).setOnClickListener {
            findViewById<ChatInputView>(R.id.inputView).hideKeyboard()
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra("antoShowKeyboard", false)) {
            findViewById<ChatInputView>(R.id.inputView)?.showKeyboard()
        }
    }

    override fun onStart() {
        super.onStart()
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
}