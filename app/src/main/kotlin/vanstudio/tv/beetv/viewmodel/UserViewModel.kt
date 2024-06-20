package vanstudio.tv.beetv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import vanstudio.tv.biliapi.http.BiliHttpApi
import vanstudio.tv.biliapi.http.entity.AuthFailureException
import vanstudio.tv.biliapi.http.entity.user.MyInfoData
import vanstudio.tv.beetv.BVApp
import vanstudio.tv.beetv.BuildConfig
import vanstudio.tv.beetv.repository.UserRepository
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vanstudio.tv.beetv.R

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private var shouldUpdateInfo = true
    private val logger = KotlinLogging.logger { }
    val isLogin get() = userRepository.isLogin
    val username get() = userRepository.username
    val face get() = userRepository.avatar

    var responseData: MyInfoData? by mutableStateOf(null)

    fun updateUserInfo(forceUpdate: Boolean = false) {
        if (!forceUpdate) {
            if (!shouldUpdateInfo || !userRepository.isLogin) return
        } else {
            if (!userRepository.isLogin) return
        }
        logger.fInfo { "Update user info" }
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.reloadAvatar()

            runCatching {
                responseData =
                    BiliHttpApi.getUserSelfInfo(sessData = Prefs.sessData).getResponseData()
                logger.fInfo { "Update user info success" }
                shouldUpdateInfo = false
                userRepository.username = responseData!!.name
                userRepository.avatar = responseData!!.face
            }.onFailure {
                when (it) {
                    is AuthFailureException -> {
                        withContext(Dispatchers.Main) {
                            BVApp.context.getString(R.string.exception_auth_failure)
                                .toast(BVApp.context)
                        }
                        logger.fInfo { "User auth failure" }
                        if (!BuildConfig.DEBUG) userRepository.logout()
                    }

                    else -> {
                        withContext(Dispatchers.Main) {
                            "获取用户信息失败：${it.message}".toast(BVApp.context)
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.logout()
        }
    }

    fun clearUserInfo() {
        userRepository.username = ""
        userRepository.avatar = ""
    }
}