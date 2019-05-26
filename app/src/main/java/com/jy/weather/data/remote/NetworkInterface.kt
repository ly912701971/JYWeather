package com.jy.weather.data.remote

import android.util.Log
import com.jy.weather.JYApplication
import com.jy.weather.entity.LiveWeather
import com.jy.weather.util.*
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * 网络接口
 *
 * Created by Yang on 2017/10/15.
 */
object NetworkInterface {

    private val db = JYApplication.cityDB

    private const val WEATHER_URL = "http://66.183.225.124/weather.php?city=%s"

    private const val QQ_USER_INFO_URL =
        "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=1109139576&openid=%s"

    private const val LIVE_WEATHER = "http://66.183.225.124/liveWeather.php"

    private const val UPLOAD_USER_INFO = "http://66.183.225.124/uploadJson.php"

    private const val UPLOAD_LIVE_WEATHER = "http://66.183.225.124/uploadLive.php"


    fun queryWeatherDataAsync(
        city: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.sendAsyncOkHttpRequest(
            String.format(WEATHER_URL, city),
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseString = (response.body() ?: return).string()
                    val jsonObject = JSONObject(responseString)
                    if (jsonObject.optInt("code") == 0) {
                        val data = jsonObject.optString("data")
                        db.setCityDataToDB(city, data)
                        onSuccess(data)
                    }
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
                    val responseString = (response.body() ?: return).string()
                    val jsonObject = JSONObject(responseString)
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

    fun queryLiveWeather(
        onSuccess: (List<LiveWeather>) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.sendAsyncOkHttpRequest(
            LIVE_WEATHER,
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseString = (response.body() ?: return).string()
                    val jsonObject = JSONObject(responseString)
                    if (jsonObject.optInt("code") == 0) {
                        onSuccess(JsonUtil.handleLiveWeatherResponse(jsonObject.optString("data")))
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
        OkHttpUtil.uploadJson(
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

    fun uploadLiveWeather(openId: String, liveText: String?, location: String, imageUri: String) {
        val file = File(ImageUtil.compress(imageUri))
        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("openId", openId)
            .addFormDataPart("liveTime", StringUtil.getDateTime())
            .addFormDataPart("liveText", liveText ?: "")
            .addFormDataPart("location", location)
            .addFormDataPart("liveImage", file.name, fileBody)
            .build()
        OkHttpUtil.uploadLiveWeather(UPLOAD_LIVE_WEATHER, requestBody, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("NetworkInterface", response.body()?.string())
            }
        })
    }
}
