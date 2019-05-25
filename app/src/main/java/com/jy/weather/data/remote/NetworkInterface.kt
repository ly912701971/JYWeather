package com.jy.weather.data.remote

import com.jy.weather.JYApplication
import com.jy.weather.util.OkHttpUtil
import com.jy.weather.util.UserUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * 网络接口
 *
 * Created by Yang on 2017/10/15.
 */
object NetworkInterface {

    private const val WEATHER_URL =
        "https://free-api.heweather.com/s6/weather?location=%s&key=4d9d9383c876415a92bb9e2fddba0b15"

    private const val QQ_USER_INFO_URL =
        "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=1109139576&openid=%s"

    private const val UPLOAD_USER_INFO = "http://66.183.225.124/uploadUserInfo.php"

    @JvmOverloads
    fun queryWeatherDataAsync(
        city: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.sendAsyncOkHttpRequest(
            String.format(WEATHER_URL, city),
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val data = (response.body() ?: return).string()
                    JYApplication.cityDB.setCityDataToDB(city, data)
                    onSuccess(data)
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onFailure(e)
                }
            }
        )
    }

    fun queryQQUserInfo(
        accessToken: String,
        openId: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.sendAsyncOkHttpRequest(
            String.format(QQ_USER_INFO_URL, accessToken, openId),
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val data = (response.body() ?: return).string()
                    val jsonObject = JSONObject(data)
                    if (jsonObject.optInt("ret") == 0) {
                        UserUtil.nickname = jsonObject.optString("nickname")
                        UserUtil.portraitUrl = jsonObject.optString("figureurl_qq_2")
                        onSuccess(UserUtil.portraitUrl)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onFailure(e)
                }
            }
        )
    }

    fun uploadUserInfo(openId: String, userName: String, portraitUrl: String) {
        OkHttpUtil.uploadUserInfo(
            UPLOAD_USER_INFO,
            JSONObject(
                mapOf(
                    "openId" to openId,
                    "userName" to userName,
                    "portraitUrl" to portraitUrl
                )
            ).toString()
        )
    }
}
