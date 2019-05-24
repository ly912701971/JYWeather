package com.jy.weather.activity

import android.Manifest
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.os.Bundle
import android.provider.Settings
import com.jy.weather.R
import com.jy.weather.databinding.ActivityCommentBinding
import com.jy.weather.navigator.CommentNavigator
import com.jy.weather.util.AlertDialogUtil
import com.jy.weather.util.PermissionUtil
import com.jy.weather.util.SnackbarUtil
import com.jy.weather.viewmodel.CommentViewModel

class CommentActivity : BaseActivity(), CommentNavigator {

    companion object {
        private const val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val LOCATION_REQUEST_CODE = 0
    }

    private lateinit var binding: ActivityCommentBinding
    private lateinit var viewModel: CommentViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        viewModel = CommentViewModel()
        binding.viewModel = viewModel
        viewModel.setImageUri(intent.getStringExtra("image_uri"))

        setupListener()

        setupSnackbarCallback()

        requestPermission()
    }

    private fun setupListener() {
        // 返回图标点击事件
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSnackbarCallback() {
        snackbarCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val snackbarObj = viewModel.snackbarObj.get() ?: return
                SnackbarUtil.showSnackBar(
                    window.decorView,
                    snackbarObj.text,
                    snackbarObj.action,
                    snackbarObj.listener
                )
            }
        }
        viewModel.snackbarObj.addOnPropertyChangedCallback(snackbarCallback)
    }

    private fun requestPermission() {
        PermissionUtil.checkPermissionAndRequest(
            this,
            PERMISSION,
            {
                viewModel.onPermissionGranted()
            },
            {
                showPermissionHintDialog()
            }
        )
    }

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionUtil.onPermissionResult(
            grantResults,
            {
                viewModel.onPermissionGranted()
            },
            {
                viewModel.onPermissionDenied()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_REQUEST_CODE) {
            viewModel.locate()
        }
    }

    private fun showPermissionHintDialog() =
        AlertDialogUtil.showDialog(
            this,
            resources.getString(R.string.request_location_permission),
            {
                PermissionUtil.requestPermission(this, PERMISSION)
            },
            {
                viewModel.onPermissionDenied()
            }
        )

    override fun startOpenGpsActivity() =
        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_REQUEST_CODE)
}
