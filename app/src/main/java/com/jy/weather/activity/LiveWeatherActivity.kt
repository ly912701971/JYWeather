package com.jy.weather.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import com.jy.weather.R
import com.jy.weather.adapter.LiveWeatherAdapter
import com.jy.weather.databinding.ActivityLiveWeatherBinding
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.*
import com.jy.weather.viewmodel.LiveWeatherViewModel
import com.jy.weather.widget.BigImageDialogFragment
import java.io.File

class LiveWeatherActivity : BaseActivity(), LiveWeatherNavigator {

    companion object {
        private const val PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val ALBUM_CODE = 0
        private const val SHOOT_CODE = 1
        private const val PUBLISH_ACTIVITY_CODE = 2
        private const val COMMENT_ACTIVITY_CODE = 3
    }

    private lateinit var binding: ActivityLiveWeatherBinding
    private lateinit var viewModel: LiveWeatherViewModel
    private lateinit var snackbarCallback: Observable.OnPropertyChangedCallback
    private lateinit var imageUri: String
    private val bigImageDialog by lazy { BigImageDialogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_weather)
        viewModel = LiveWeatherViewModel()
        binding.viewModel = viewModel

        setupView()

        setupSnackbarCallback()

        viewModel.autoLogin()

        viewModel.queryLiveWeather()
    }

    private fun setupView() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryLiveWeather()
        }

        binding.lvLiveWeather.adapter = LiveWeatherAdapter(this, this)
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

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    override fun onDestroy() {
        viewModel.snackbarObj.removeOnPropertyChangedCallback(snackbarCallback)
        super.onDestroy()
    }

    override fun requestPermission() {
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

    override fun showChooseImageDialog() =
        AlertDialog.Builder(this, AlertDialogUtil.getTheme())
            .setTitle(getString(R.string.please_choose))
            .setSingleChoiceItems(viewModel.choosePhotoMode, -1) { dialog, index ->
                when (index) {
                    0 -> startAlbumPage()
                    1 -> startShootPage()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()

    override fun showLoginHintDialog() {
        AlertDialogUtil.showDialog(
            this,
            getString(R.string.login_hint),
            {
                showLoginDialog()
            }
        )
    }

    private fun showPermissionHintDialog() =
        AlertDialogUtil.showDialog(
            this,
            getString(R.string.request_write_permission),
            {
                PermissionUtil.requestPermission(this, PERMISSION)
            }
        )

    /**
     * 打开相册
     */
    private fun startAlbumPage() =
        startActivityForResult(
            Intent(Intent.ACTION_PICK, null).apply {
                setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            },
            ALBUM_CODE
        )

    /**
     * 拍照
     */
    private fun startShootPage() {
        val photoDir = File(
            "${Environment.getExternalStorageDirectory()}${File.separator}photoCache${File.separator}")
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }

        val photoFile = File(photoDir, "${System.currentTimeMillis()}.jpg")
        imageUri = photoFile.absolutePath
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT,
                    Provider7Util.getUriForFile(this@LiveWeatherActivity, photoFile))
            },
            SHOOT_CODE
        )
    }

    override fun showLoginDialog() {
        AlertDialog.Builder(this, AlertDialogUtil.getTheme())
            .setTitle(getString(R.string.please_choose))
            .setSingleChoiceItems(viewModel.loginType, -1) { dialog, index ->
                when (index) {
                    0 -> viewModel.loginViaQQ(this)
                    1 -> viewModel.loginViaWX()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun showLogoutDialog() =
        AlertDialogUtil.showDialog(
            this,
            getString(R.string.confirm_logout),
            {
                UserUtil.logout()
                viewModel.logout()
            }
        )

    override fun showBigImageDialog(url: String) {
        bigImageDialog.setImageUrl(url)
        bigImageDialog.show(supportFragmentManager, url)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionUtil.onPermissionResult(
            grantResults,
            {
                showChooseImageDialog()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ALBUM_CODE -> {
                    imageUri = viewModel.getPhotoPath(data?.data ?: return)
                    startPublishActivity()
                }

                SHOOT_CODE -> startPublishActivity()

                PUBLISH_ACTIVITY_CODE -> {
                    val status = data?.extras?.get("update_status").toString()
                    viewModel.onUpdateLiveWeatherResult(status)
                }

                COMMENT_ACTIVITY_CODE -> {
                    val commentText = data?.extras?.get("comment_text").toString()
                    viewModel.uploadComment(commentText)
                }

                UserUtil.REQUEST_LOGIN -> UserUtil.onActivityResultData(requestCode, resultCode,
                    data)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startPublishActivity() =
        startActivityForResult(
            Intent(this, PublishLiveActivity::class.java).apply {
                putExtra("image_uri", imageUri)
            },
            PUBLISH_ACTIVITY_CODE
        )

    override fun startCommentActivity(liveId: Int) {
        viewModel.liveId = liveId
        startActivityForResult(
            Intent(this, CommentActivity::
            class.java),
            COMMENT_ACTIVITY_CODE
        )
    }
}