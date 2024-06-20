package vanstudio.tv.beetv.network

import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class GithubApiTest {
    @Test
    fun `get latest release build`() = runBlocking {
        println(GithubApi.getLatestReleaseBuild())
    }

    @Test
    fun `get latest pre-release build`() = runBlocking {
        println(GithubApi.getLatestPreReleaseBuild())
    }
}