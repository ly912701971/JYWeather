package com.jy.weather.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import com.jy.weather.R
import com.jy.weather.databinding.ActivityLiveWeatherBinding
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.AlertDialogUtil
import com.jy.weather.util.PermissionUtil
import com.jy.weather.util.Provider7Util
import com.jy.weather.viewmodel.LiveWeatherViewModel
import java.io.File

class LiveWeatherActivity : BaseActivity(), LiveWeatherNavigator {

    companion object {
        private const val PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val ALBUM_CODE = 0
        private const val SHOOT_CODE = 1
        private const val COMMENT_ACTIVITY_CODE = 2
    }

    private lateinit var binding: ActivityLiveWeatherBinding
    private lateinit var viewModel: LiveWeatherViewModel
    private lateinit var imageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_weather)
        viewModel = LiveWeatherViewModel()
        binding.viewModel = viewModel

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_live_weather, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_take_photo) {
            requestPermission()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestPermission() {
        PermissionUtil.checkPermissionAndRequest(
            this,
            PERMISSION,
            {
                showChooseImageDialog()
            },
            {
                showPermissionHintDialog()
            }
        )
    }

    override fun showChooseImageDialog() {
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
        val photoDir = File("${Environment.getExternalStorageDirectory()}${File.separator}photoCache${File.separator}")
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionUtil.onPermissionResult(
            grantResults,
            {
                showChooseImageDialog()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ALBUM_CODE -> {
                    getPhotoPath(data?.data ?: return)
                    startCommentActivity()
                }
                SHOOT_CODE -> {
                    startCommentActivity()
                }
                COMMENT_ACTIVITY_CODE -> {

                }
            }
        }
    }

    private fun getPhotoPath(uri: Uri) {
        imageUri = when {
            DocumentsContract.isDocumentUri(this, uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                when (uri.authority) {
                    "com.android.providers.media.documents" -> {
                        val id = docId.split(Regex(":"))[1]// 解析出数字格式id
                        val selection = "${MediaStore.Images.Media._ID}=$id"
                        getImagePath(uri, selection)
                    }
                    "com.android.providers.downloads.documents" -> {
                        val contentUri =
                            ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                        getImagePath(contentUri, null)
                    }
                    else -> ""
                }
            }
            uri.scheme.equals("content", true) -> getImagePath(uri, null)
            uri.scheme.equals("file", true) -> uri.path ?: return
            else -> ""
        }
    }

    private fun getImagePath(uri: Uri, selection: String?): String {
        val cursor = contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
        }
        cursor?.close()
        return ""
    }

    private fun startCommentActivity() =
        startActivityForResult(
            Intent(this, CommentActivity::class.java).apply {
                putExtra("image_uri", imageUri)
            },
            COMMENT_ACTIVITY_CODE
        )
}