package vanstudio.tv.beetv

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.analytics.ktx.analytics
//import com.google.firebase.ktx.Firebase
import de.schnettler.datastore.manager.DataStoreManager
import vanstudio.tv.biliapi.http.BiliHttpProxyApi
import vanstudio.tv.biliapi.repositories.AuthRepository
import vanstudio.tv.biliapi.repositories.ChannelRepository
import vanstudio.tv.biliapi.repositories.FavoriteRepository
import vanstudio.tv.biliapi.repositories.HistoryRepository
import vanstudio.tv.biliapi.repositories.IndexRepository
import vanstudio.tv.biliapi.repositories.LoginRepository
import vanstudio.tv.biliapi.repositories.RecommendVideoRepository
import vanstudio.tv.biliapi.repositories.SearchRepository
import vanstudio.tv.biliapi.repositories.SeasonRepository
import vanstudio.tv.biliapi.repositories.VideoDetailRepository
import vanstudio.tv.biliapi.repositories.VideoPlayRepository
import vanstudio.tv.beetv.BuildConfig
import vanstudio.tv.beetv.dao.AppDatabase
import vanstudio.tv.beetv.entity.AuthData
import vanstudio.tv.beetv.entity.db.UserDB
import vanstudio.tv.beetv.network.HttpServer
import vanstudio.tv.beetv.repository.UserRepository
import vanstudio.tv.beetv.repository.VideoInfoRepository
import vanstudio.tv.beetv.screen.user.UserSwitchViewModel
import vanstudio.tv.beetv.util.LogCatcherUtil
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.viewmodel.PlayerViewModel
import vanstudio.tv.beetv.viewmodel.TagViewModel
import vanstudio.tv.beetv.viewmodel.UserViewModel
import vanstudio.tv.beetv.viewmodel.VideoPlayerV3ViewModel
import vanstudio.tv.beetv.viewmodel.home.AnimeViewModel
import vanstudio.tv.beetv.viewmodel.home.DynamicViewModel
import vanstudio.tv.beetv.viewmodel.home.PopularViewModel
import vanstudio.tv.beetv.viewmodel.home.RecommendViewModel
import vanstudio.tv.beetv.viewmodel.index.AnimeIndexViewModel
import vanstudio.tv.beetv.viewmodel.login.AppQrLoginViewModel
import vanstudio.tv.beetv.viewmodel.login.SmsLoginViewModel
import vanstudio.tv.beetv.viewmodel.search.SearchInputViewModel
import vanstudio.tv.beetv.viewmodel.search.SearchResultViewModel
import vanstudio.tv.beetv.viewmodel.user.FavoriteViewModel
import vanstudio.tv.beetv.viewmodel.user.FollowViewModel
import vanstudio.tv.beetv.viewmodel.user.FollowingSeasonViewModel
import vanstudio.tv.beetv.viewmodel.user.HistoryViewModel
import vanstudio.tv.beetv.viewmodel.user.UpInfoViewModel
import vanstudio.tv.beetv.viewmodel.video.VideoDetailViewModel
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.slf4j.impl.HandroidLoggerAdapter

class BVApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var dataStoreManager: DataStoreManager
        lateinit var koinApplication: KoinApplication
//        lateinit var firebaseAnalytics: FirebaseAnalytics
        var instance: BVApp? = null

        fun getAppDatabase(context: Context = Companion.context) = AppDatabase.getDatabase(context)
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG
        dataStoreManager = DataStoreManager(applicationContext.dataStore)
        koinApplication = startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BVApp)
            modules(appModule)
        }
//        firebaseAnalytics = Firebase.analytics
        LogCatcherUtil.installLogCatcher()
        initRepository()
        initProxy()
        instance = this
        updateMigration()
        HttpServer.startServer()
    }

    fun initRepository() {
        val channelRepository by koinApplication.koin.inject<ChannelRepository>()
        channelRepository.initDefaultChannel(Prefs.accessToken, Prefs.buvid)

        val authRepository by koinApplication.koin.inject<AuthRepository>()
        authRepository.sessionData = Prefs.sessData.takeIf { it.isNotEmpty() }
        authRepository.biliJct = Prefs.biliJct.takeIf { it.isNotEmpty() }
        authRepository.accessToken = Prefs.accessToken.takeIf { it.isNotEmpty() }
        authRepository.mid = Prefs.uid.takeIf { it != 0L }
        authRepository.buvid3 = Prefs.buvid3
        authRepository.buvid = Prefs.buvid
    }

    fun initProxy() {
        if (Prefs.enableProxy) {
            BiliHttpProxyApi.createClient(Prefs.proxyHttpServer)

            val channelRepository by koinApplication.koin.inject<ChannelRepository>()
            runCatching {
                channelRepository.initProxyChannel(
                    Prefs.accessToken,
                    Prefs.buvid,
                    Prefs.proxyGRPCServer
                )
            }
        }
    }

    private fun updateMigration() {
        val lastVersionCode = Prefs.lastVersionCode
        if (lastVersionCode >= BuildConfig.VERSION_CODE) return
        Log.i("BVApp", "updateMigration from $lastVersionCode")
        if (lastVersionCode < 576) {
            // 从 Prefs 中读取登录数据写入 UserDB
            if (Prefs.isLogin) {
                runBlocking {
                    val existedUser = getAppDatabase().userDao().findUserByUid(Prefs.uid)
                    if (existedUser == null) {
                        val user = UserDB(
                            uid = Prefs.uid,
                            username = "Unknown",
                            avatar = "",
                            auth = AuthData.fromPrefs().toJson()
                        )
                        getAppDatabase().userDao().insert(user)
                    }
                }
            }
        }
        Prefs.lastVersionCode = BuildConfig.VERSION_CODE
    }
}

val appModule = module {
    single { AuthRepository() }
    single { UserRepository(get()) }
    single { LoginRepository() }
    single { VideoInfoRepository() }
    single { ChannelRepository() }
    single { FavoriteRepository(get()) }
    single { HistoryRepository(get(), get()) }
    single { SearchRepository(get(), get()) }
    single { VideoPlayRepository(get(), get()) }
    single { RecommendVideoRepository(get(), get()) }
    single { VideoDetailRepository(get(), get(), get()) }
    single { SeasonRepository(get()) }
    single { vanstudio.tv.biliapi.repositories.UserRepository(get(), get()) }
    single { IndexRepository() }
    viewModel { DynamicViewModel(get(), get()) }
    viewModel { RecommendViewModel(get()) }
    viewModel { PopularViewModel(get()) }
    viewModel { AppQrLoginViewModel(get(), get()) }
    viewModel { SmsLoginViewModel(get(), get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { HistoryViewModel(get(), get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { UpInfoViewModel(get()) }
    viewModel { FollowViewModel(get()) }
    viewModel { SearchInputViewModel(get()) }
    viewModel { SearchResultViewModel(get()) }
    viewModel { AnimeViewModel() }
    viewModel { FollowingSeasonViewModel(get()) }
    viewModel { TagViewModel() }
    viewModel { VideoPlayerV3ViewModel(get(), get()) }
    viewModel { VideoDetailViewModel(get()) }
    viewModel { UserSwitchViewModel(get()) }
    viewModel { AnimeIndexViewModel(get()) }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")
