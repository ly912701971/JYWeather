package com.jy.weather.data.remote

import android.util.Log
import com.jy.weather.JYApplication
import com.jy.weather.entity.LiveWeather
import com.jy.weather.util.ImageUtil
import com.jy.weather.util.JsonUtil
import com.jy.weather.util.OkHttpUtil
import com.jy.weather.util.StringUtil
import com.jy.weather.util.UserUtil
import java.io.File
import java.io.IOException
import kotlin.Result.Companion
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject

/**
 * 网络接口
 *
 * Created by Yang on 2017/10/15.
 */
object NetworkInterface {

    private val db = JYApplication.cityDB

    // test server host
//    private const val SERVER_HOST = "66.183.228.71"

    // publish server host
    private const val SERVER_HOST = "47.102.221.69"

    private const val BASE_URL =
        "https://devapi.qweather.com/v7/weather/%s?key=32cb7c9c6bd6432eaf054f3e347160aa&location=%s"

    private const val INDEX_URL =
        "https://devapi.qweather.com/v7/indices/1d?key=32cb7c9c6bd6432eaf054f3e347160aa&type=1,2,3,5,6,8,9,10&location=%s"

    private const val LOCATION_URL =
        "https://geoapi.qweather.com/v2/city/lookup?key=32cb7c9c6bd6432eaf054f3e347160aa&number=20&location=%s"

    private const val QQ_USER_INFO_URL =
        "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=1109139576&openid=%s"

    private const val LIVE_WEATHER = "http://$SERVER_HOST/liveWeather.php"

    private const val UPLOAD_USER_INFO = "http://$SERVER_HOST/uploadUserInfo.php"

    private const val UPLOAD_LIVE_WEATHER = "http://$SERVER_HOST/uploadLive.php"

    private const val UPLOAD_COMMENT = "http://$SERVER_HOST/uploadComment.php"

    private const val UPLOAD_LIKE = "http://$SERVER_HOST/uploadLike.php"

    private suspend fun queryData(url: String): String? {
        if (url.contains("lookup")) {
            Log.i("llllyyyy", "req lookup")
        }
        return suspendCancellableCoroutine {
            OkHttpUtil.sendAsyncOkHttpRequest(
                url,
                object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseString = (response.body ?: return).string()
                        it.resumeWith(Result.success(responseString))
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        it.resumeWith(Companion.failure(e))
                    }
                }
            )
        }
    }

    suspend fun queryNowData(cityId: String) = queryData(String.format(BASE_URL, "now", cityId))

    suspend fun queryHourlyData(cityId: String) = queryData(String.format(BASE_URL, "24h", cityId))

    suspend fun queryDailyData(cityId: String) = queryData(String.format(BASE_URL, "7d", cityId))

    suspend fun queryIndexData(cityId: String) = queryData(String.format(INDEX_URL, cityId))

    suspend fun queryLocationData(city: String) = queryData(String.format(LOCATION_URL, city))

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
                    val responseString = (response.body ?: return).string()
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
            })
    }

    fun queryLiveWeather(
        pageIndex: Int = 0,
        pageSize: Int = 20,
        fromId: Int = 0,
        openId: String = "",
        onSuccess: (List<LiveWeather>) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.uploadJsonAsync(LIVE_WEATHER, JSONObject(
            mapOf(
                "pageIndex" to pageIndex,
                "pageSize" to pageSize,
                "fromId" to fromId,
                "openId" to openId
            )
        ).toString(), object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseString = (response.body ?: return).string()
                val jsonObject = JSONObject(responseString)
                if (jsonObject.optInt("code") == 0) {
                    val data = jsonObject.optString("data")
                    db.liveWeatherCache = data
                    onSuccess(JsonUtil.handleLiveWeatherResponse(data))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onFailure(e)
            }
        })
    }

    fun uploadUserInfo(
        openId: String,
        userName: String,
        portraitUrl: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.uploadJsonAsync(UPLOAD_USER_INFO, JSONObject(
            mapOf(
                "openId" to openId, "userName" to userName, "portraitUrl" to portraitUrl
            )
        ).toString(), object : Callback {
            override fun onResponse(call: Call, response: Response) {
                onSuccess()
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onFailure(e)
            }
        })
    }

    fun uploadLiveWeather(
        openId: String,
        liveText: String?,
        location: String,
        imageUri: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val file = File(ImageUtil.compress(imageUri))
        val fileBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
        val requestBody =
            MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("openId", openId)
                .addFormDataPart("liveTime", StringUtil.getDateTime())
                .addFormDataPart("liveText", liveText ?: "").addFormDataPart("location", location)
                .addFormDataPart("liveImage", file.name, fileBody).build()
        OkHttpUtil.uploadLiveWeather(UPLOAD_LIVE_WEATHER, requestBody, object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (file.exists() && file.isFile) {
                    file.delete()
                }

                val responseString = (response.body ?: return).string()
                onSuccess(responseString)
            }

            override fun onFailure(call: Call, e: IOException) {
                if (file.exists() && file.isFile) {
                    file.delete()
                }

                e.printStackTrace()
                onFailure(e)
            }
        })
    }

    fun uploadComment(
        openId: String,
        liveId: String,
        commentText: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.uploadJsonAsync(UPLOAD_COMMENT, JSONObject(
            mapOf(
                "openId" to openId, "liveId" to liveId, "commentText" to commentText
            )
        ).toString(), object : Callback {
            override fun onResponse(call: Call, response: Response) {
                onSuccess()
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onFailure(e)
            }
        })
    }

    fun uploadLike(
        openId: String,
        liveId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        OkHttpUtil.uploadJsonAsync(UPLOAD_LIKE, JSONObject(
            mapOf(
                "openId" to openId, "liveId" to liveId
            )
        ).toString(), object : Callback {
            override fun onResponse(call: Call, response: Response) {
                (response.body ?: return).string()
                onSuccess()
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onFailure(e)
            }
        })
    }
}
