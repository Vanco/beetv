package vanstudio.tv.beetv.viewmodel.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import vanstudio.tv.biliapi.http.util.generateBuvid
import vanstudio.tv.biliapi.repositories.LoginRepository
import vanstudio.tv.biliapi.repositories.SendSmsState
import vanstudio.tv.beetv.BVApp
import vanstudio.tv.beetv.entity.AuthData
import vanstudio.tv.beetv.repository.UserRepository
import vanstudio.tv.beetv.util.fDebug
import vanstudio.tv.beetv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL

class SmsLoginViewModel(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val logger = KotlinLogging.logger { }
    var sendSmsState by mutableStateOf(SendSmsState.Ready)

    private var phone: Long = 0
    private val loginSessionId = loginRepository.generateLoginSessionId()
    private var recaptchaToken: String? = null
    var geetestChallenge: String? = null
    var geetestValidate: String? = null
    private var geetestGt: String? = null
    private val buvid = generateBuvid()
    private var captchaKey: String? = null

    suspend fun sendSms(
        phone: Long,
        onCaptcha: (challenge: String, gt: String) -> Unit
    ) {
        this.phone = phone
        logger.info { "Send sms to $phone" }
        runCatching {
            val sendSmsResult = loginRepository.requestSms(
                phone, loginSessionId, buvid, recaptchaToken, geetestChallenge, geetestValidate
            )
            when (sendSmsResult.state) {
                SendSmsState.Ready -> {
                    logger.info { "this state should be here: $sendSmsState" }
                    sendSmsState = sendSmsResult.state
                }

                SendSmsState.Error -> {
                    logger.warn { "Send sms failed: ${sendSmsResult.message}" }
                    sendSmsState = sendSmsResult.state
                    withContext(Dispatchers.Main) {
                        "发送短信失败：${sendSmsResult.message}".toast(BVApp.context)
                    }
                    clearCaptchaData()
                }

                SendSmsState.Success -> {
                    logger.info { "Send sms success" }
                    sendSmsState = sendSmsResult.state
                    captchaKey = sendSmsResult.captchaKey
                    withContext(Dispatchers.Main) {
                        "验证码已发送".toast(BVApp.context)
                    }
                }

                SendSmsState.RecaptchaRequire -> {
                    logger.info { "Require manual recaptcha" }
                    logger.info { "recaptcha url: ${sendSmsResult.recaptchaUrl}" }

                    URL(sendSmsResult.recaptchaUrl).query.split("&").forEach {
                        val (key, value) = it.split("=")
                        when (key) {
                            "recaptcha_token" -> recaptchaToken = value
                            "gee_gt" -> geetestGt = value
                            "gee_challenge" -> geetestChallenge = value
                        }
                    }

                    logger.info { "recaptchaToken: $recaptchaToken" }
                    logger.info { "geetestGt: $geetestGt" }
                    logger.info { "geetestChallenge: $geetestChallenge" }
                    onCaptcha(geetestChallenge!!, geetestGt!!)
                    sendSmsState = sendSmsResult.state
                }
            }
        }.onFailure {
            logger.warn { "Send sms failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "发送短信失败：${it.message}".toast(BVApp.context)
            }
            clearCaptchaData()
        }
    }

    suspend fun loginWithSms(code: Int, onSuccess: () -> Unit) {
        logger.info { "Login with sms code: $code" }
        runCatching {
            val loginResult = loginRepository.loginWithSms(
                phone = phone,
                loginSessionId = loginSessionId,
                code = code,
                captchaKey = captchaKey!!
            )
            if (loginResult.status == 0) {
                val authData = AuthData(
                    uid = loginResult.dedeUserId,
                    uidCkMd5 = loginResult.dedeUserIdCkMd5,
                    sid = loginResult.sid,
                    sessData = loginResult.sessData,
                    biliJct = loginResult.biliJct,
                    tokenExpiredData = loginResult.expiredDate.time,
                    accessToken = loginResult.accessToken,
                    refreshToken = loginResult.refreshToken
                )
                userRepository.addUser(authData)

                withContext(Dispatchers.Main) {
                    "登录成功".toast(BVApp.context)
                }
                logger.info { "Login with sms success" }
                logger.fDebug { "$loginResult" }
                onSuccess()
            } else {
                logger.warn { "Login with sms return a unknown response: [status=${loginResult.status}, message=${loginResult.message}]" }
                withContext(Dispatchers.Main) {
                    "未知情况：${loginResult.message}".toast(BVApp.context)
                }
            }
        }.onFailure {
            logger.warn { "Login with sms failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "短信登录失败：${it.message}".toast(BVApp.context)
            }
        }
    }

    fun clearCaptchaData() {
        logger.info { "Clear captcha data" }
        recaptchaToken = null
        geetestChallenge = null
        geetestValidate = null
        sendSmsState = SendSmsState.Ready
    }
}

@Serializable
data class GeetestResult(
    @SerialName("geetest_challenge")
    val geetestChallenge: String,
    @SerialName("geetest_validate")
    val geetestValidate: String,
    @SerialName("geetest_seccode")
    val geetestSeccode: String
)