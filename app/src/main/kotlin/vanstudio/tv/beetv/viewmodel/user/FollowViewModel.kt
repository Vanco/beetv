package vanstudio.tv.beetv.viewmodel.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import vanstudio.tv.biliapi.entity.user.FollowedUser
import vanstudio.tv.biliapi.repositories.UserRepository
import vanstudio.tv.beetv.util.Prefs
import vanstudio.tv.beetv.util.fInfo
import vanstudio.tv.beetv.util.swapList
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

class FollowViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    var followedUsers = mutableStateListOf<FollowedUser>()
    var updating by mutableStateOf(true)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            initFollowedUsers()
        }
    }

    private suspend fun initFollowedUsers() {
        runCatching {
            logger.fInfo { "Init followed users" }
            val followedUserList = userRepository.getFollowedUsers(
                mid = Prefs.uid,
                preferApiType = Prefs.apiType
            )
            logger.fInfo { "Followed user count: ${followedUserList.size}" }
            followedUsers.swapList(followedUserList)
            followedUsers.swapList(sortUsers())
            logger.fInfo { "Load followed user finish" }
        }
        updating = false
    }

    private fun sortUsers(): List<FollowedUser> {
        val sortedList = mutableStateListOf<FollowedUser>()
        val usersStartWithoutChinese =
            followedUsers.filter { Regex("^[A-Za-z0-9_-]").containsMatchIn(it.name) }
                .toMutableList()
        val usersStartWithChinese =
            (followedUsers - usersStartWithoutChinese.toSet()).toMutableList()

        usersStartWithoutChinese.sortWith { o1, o2 ->
            Collator.getInstance(Locale.CHINA).compare(o1.name, o2.name)
        }
        usersStartWithChinese.sortWith { o1, o2 ->
            Collator.getInstance(Locale.CHINA).compare(o1.name, o2.name)
        }

        logger.info { "sorted user which start without chinese: ${usersStartWithoutChinese.map { it.name }}" }
        logger.info { "sorted user which start with chinese: ${usersStartWithChinese.map { it.name }}" }

        sortedList.addAll(usersStartWithoutChinese)
        sortedList.addAll(usersStartWithChinese)

        return sortedList
    }
}