object AppConfiguration {
    const val appId = "vanstudio.tv.beetv"
    const val compileSdk = 34
    const val minSdk = 21
    const val targetSdk = 34
    private const val major = 1
    private const val minor = 0
    private const val patch = 1
    private const val bugFix = 0

    @Suppress("KotlinConstantConditions")
    val versionName: String by lazy {
        "$major.$minor.$patch${".$bugFix".takeIf { bugFix != 0 } ?: ""}" +
                ".r${versionCode}.${"git rev-list HEAD --abbrev-commit --max-count=1".exec()}"
    }
    val versionCode: Int by lazy { "git rev-list --count HEAD".exec().toInt() }
    const val libVLCVersion = "3.0.18"
}

fun String.exec() = String(Runtime.getRuntime().exec(this.split(" ").toTypedArray()).inputStream.readBytes()).trim()