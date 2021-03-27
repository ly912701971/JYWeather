package com.jy.weather.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.jy.weather.R
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.entity.Comment
import com.jy.weather.entity.LiveWeather
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.NetworkUtil
import com.jy.weather.util.UserUtil
import java.lang.ref.WeakReference

class LiveWeatherItemViewModel(liveWeather: LiveWeather, navigator: LiveWeatherNavigator) {

    private val navigator = WeakReference(navigator)
    private val liveId = liveWeather.liveId
    private var hasLiked = liveWeather.hasLiked == 1

    val userName: ObservableField<String> = ObservableField(liveWeather.userName)
    val userPortrait: ObservableField<String> = ObservableField(liveWeather.userPortrait)
    val liveTime: ObservableField<String> = ObservableField(liveWeather.liveTime)
    val liveText: ObservableField<String> = ObservableField(liveWeather.liveText)
    val location: ObservableField<String> = ObservableField(liveWeather.location)
    val liveUrl: ObservableField<String> = ObservableField(liveWeather.liveUrl)
    val likeNum: ObservableField<Int> = ObservableField(liveWeather.likeNum)
    val likeStatusResId: ObservableField<Int> = ObservableField(R.drawable.ic_not_praise)
    val comments: ObservableList<Comment> = ObservableArrayList<Comment>().apply {
        addAll(liveWeather.commentArray)
    }

    val commentNum: ObservableField<String> =
        ObservableField(liveWeather.commentArray.size.toString())
    val liveTextVisibility: ObservableBoolean = ObservableBoolean(liveWeather.liveText.isNotEmpty())
    val commentVisibility: ObservableBoolean =
        ObservableBoolean(liveWeather.commentArray.isNotEmpty())

    fun onImageClick() {
        navigator.get()?.showBigImageDialog(liveUrl.get()!!)
    }

    fun onLikeClick() {
        if (!UserUtil.hasLogin()) {
            navigator.get()?.showLoginHintDialog()
        } else {
            if (NetworkUtil.isNetworkAvailable()) {
                NetworkInterface.uploadLike(
                    UserUtil.openId,
                    liveId.toString(),
                    {
                        if (hasLiked) {
                            likeNum.set(likeNum.get()?.minus(1))
                            hasLiked = false
                            likeStatusResId.set(R.drawable.ic_not_praise)
                        } else {
                            likeNum.set(likeNum.get()?.plus(1))
                            hasLiked = true
                            likeStatusResId.set(R.drawable.ic_praise)
                        }
                    }
                )
            }
        }
    }

    fun onCommentClick() {
        if (!UserUtil.hasLogin()) {
            navigator.get()?.showLoginHintDialog()
        } else {
            navigator.get()?.startCommentActivity(liveId)
        }
    }
}