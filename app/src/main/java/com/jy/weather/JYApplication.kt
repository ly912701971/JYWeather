package com.jy.weather

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

import com.jy.weather.data.local.CityDB
import com.tencent.bugly.Bugly
import com.tendcloud.tenddata.TCAgent
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

        // Bugly初始化
        Bugly.init(applicationContext, "dfbeff5d40", true)

        // TalkingData初始化
        TCAgent.LOG_ON = true
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this, "09AAE34B970B4C96BBE79940F841311E", "jyweather")
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        MultiDex.install(base)
    }
}
