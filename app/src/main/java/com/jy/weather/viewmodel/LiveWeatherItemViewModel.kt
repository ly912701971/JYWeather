package com.jy.weather.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.entity.Comment
import com.jy.weather.entity.LiveWeather
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.UserUtil
import java.lang.ref.WeakReference

class LiveWeatherItemViewModel(liveWeather: LiveWeather, navigator: LiveWeatherNavigator) {

    private val navigator = WeakReference(navigator)
    private val liveId = liveWeather.liveId

    val userName: ObservableField<String> = ObservableField(liveWeather.userName)
    val userPortrait: ObservableField<String> = ObservableField(liveWeather.userPortrait)
    val liveTime: ObservableField<String> = ObservableField(liveWeather.liveTime)
    val liveText: ObservableField<String> = ObservableField(liveWeather.liveText)
    val location: ObservableField<String> = ObservableField(liveWeather.location)
    val liveUrl: ObservableField<String> = ObservableField(liveWeather.liveUrl)
    val likeNum: ObservableField<Int> = ObservableField(liveWeather.likeNum)
    val hasLiked: ObservableBoolean = ObservableBoolean(liveWeather.hasLiked == 1)
    val comments: ObservableList<Comment> = ObservableArrayList<Comment>().apply {
        addAll(liveWeather.commentArray)
    }

    val commentNum: ObservableField<String> = ObservableField(liveWeather.commentArray.size.toString())
    val liveTextVisibility: ObservableBoolean = ObservableBoolean(liveWeather.liveText.isNotEmpty())
    val commentVisibility: ObservableBoolean = ObservableBoolean(liveWeather.commentArray.isNotEmpty())

    fun onImageClick() {
        navigator.get()?.showBigImageDialog(liveUrl.get()!!)
    }

    fun onLikeClick() {
        if (!UserUtil.hasLogin()) {
            navigator.get()?.showLoginHintDialog()
        } else {
            NetworkInterface.uploadLike(
                UserUtil.openId,
                liveId.toString(),
                {
                    if (hasLiked.get()) {
                        likeNum.set(likeNum.get()?.minus(1))
                        hasLiked.set(false)
                    } else {
                        likeNum.set(likeNum.get()?.plus(1))
                        hasLiked.set(true)
                    }
                }
            )
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