package com.jy.weather.util

import android.app.Activity
import android.content.Intent
import com.jy.weather.JYApplication
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject

object UserUtil {

    const val REQUEST_LOGIN = Constants.REQUEST_LOGIN
    private const val APP_ID = "1109139576"
    private const val LOGOUT = "logout"

    private val tencent: Tencent by lazy {
        Tencent.createInstance(APP_ID, JYApplication.INSTANCE)
    }

    var accessToken = LOGOUT
    var openId = LOGOUT
    var nickname = ""
    var portraitUrl = ""

    fun autoLogin() {
        if (tencent.checkSessionValid(APP_ID)) {
            val jsonObject = tencent.loadSession(APP_ID)
            tencent.initSessionCache(jsonObject)
            setupUserInfo(jsonObject)
        }
    }

    fun login(
        activity: Activity,
        onLoginSucceed: () -> Unit,
        onLoginFailed: () -> Unit
    ) {
        tencent.checkSessionValid(APP_ID)
        if (!tencent.isSessionValid) {
            tencent.login(activity, "get_simple_userinfo", object : IUiListener {

                override fun onComplete(p0: Any?) {
                    setupUserInfo(JSONObject(p0.toString()))
                    onLoginSucceed()
                }

                override fun onError(p0: UiError?) {
                    onLoginFailed()
                }

                override fun onCancel() {}
            })
        }
    }

    fun logout() {
        tencent.logout(JYApplication.INSTANCE)
        accessToken = LOGOUT
        openId = LOGOUT
    }

    fun onActivityResultData(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        Tencent.onActivityResultData(requestCode, resultCode, data, object : IUiListener {
            override fun onComplete(p0: Any?) {}
            override fun onError(p0: UiError?) {}
            override fun onCancel() {}
        })
    }

    fun hasLogin() = accessToken != LOGOUT

    private fun setupUserInfo(jsonObject: JSONObject) {
        if (jsonObject.optInt("ret") == 0) {
            accessToken = jsonObject.optString("access_token", LOGOUT)
            openId = jsonObject.optString("openid", LOGOUT)
        }
    }
}