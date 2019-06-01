package com.jy.weather.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.jy.weather.R
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : Activity() {

    private val manager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setContentView(R.layout.activity_comment)

        view_blank.setOnClickListener { finish() }

        tv_send.setOnClickListener {
            setResult(
                RESULT_OK,
                Intent().putExtra("comment_text", et_comment.text)
            )
            finish()
        }
    }

    private fun setStatusBarColor() =
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed(
            {
                showOrHideSoftInput()
            },
            100
        )
    }

    private fun showOrHideSoftInput() =
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}