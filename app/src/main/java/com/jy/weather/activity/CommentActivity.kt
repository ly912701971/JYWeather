package com.jy.weather.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.jy.weather.R
import com.jy.weather.databinding.ActivityCommentBinding
import com.jy.weather.viewmodel.CommentViewModel

class CommentActivity : BaseActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var viewModel: CommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        viewModel = CommentViewModel()
        binding.viewModel = viewModel
        viewModel.setImageUri(intent.getStringExtra("image_uri"))
    }
}
