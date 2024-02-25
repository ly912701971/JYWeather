package com.jy.weather

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.jy.weather.data.local.CityDB
import org.litepal.LitePal

/**
 * 整个应用的启动文件
 * 对整个应用进行初始化
 *
 * Created by Yang on 2018/1/5.
 */
class JYApplication : Application() {

    companion object {
        lateinit var INSTANCE: JYApplication
        lateinit var cityDB: CityDB
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        cityDB = CityDB(this)

        // litepal数据库
        LitePal.initialize(this)
        Log.i("JYApplication", "test")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}
