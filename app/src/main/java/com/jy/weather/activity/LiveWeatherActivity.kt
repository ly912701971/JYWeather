package com.jy.weather.activity

import android.app.AlertDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import com.jy.weather.R
import com.jy.weather.databinding.ActivityLiveWeatherBinding
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.AlertDialogUtil
import com.jy.weather.util.Provider7Util
import com.jy.weather.viewmodel.LiveWeatherViewModel
import java.io.File

class LiveWeatherActivity : BaseActivity(), LiveWeatherNavigator {

    private lateinit var binding: ActivityLiveWeatherBinding
    private lateinit var viewModel: LiveWeatherViewModel
    private lateinit var photoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_weather)
        viewModel = LiveWeatherViewModel()
        binding.viewModel = viewModel

        setupToolbar()
    }

    override fun onResume() {
        super.onResume()

        viewModel.start(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_live_weather, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_take_photo) {
            showChoosePhotoMode()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showChoosePhotoMode() {
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

    /**
     * 打开相册
     */
    private fun startAlbumPage() =
        startActivityForResult(
            Intent(Intent.ACTION_PICK, null).apply {
                setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            },
            viewModel.ALBUM_MODE
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
        photoPath = photoFile.absolutePath
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT,
                    Provider7Util.getUriForFile(this@LiveWeatherActivity, photoFile))
            },
            viewModel.SHOOT_MODE
        )
    }
}