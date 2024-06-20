pluginManagement {
    repositories {
        google()
//        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.name) {
                "crashlytics" -> useModule("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
            }
        }
    }
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2/")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
//        maven("https://artifact.bytedance.com/repository/releases/")
    }
    versionCatalogs {
        create("androidx") { from(files("gradle/androidx.versions.toml")) }
        create("gradleLibs") { from(files("gradle/gradle.versions.toml")) }
    }
}
rootProject.name = "VNet"
include(":app")
include(":bili-api")
include(":bili-subtitle")
include(":bv-player")
include(":libs:av1Decoder")
include(":libs:libVLC")
include(":bili-api-grpc")
