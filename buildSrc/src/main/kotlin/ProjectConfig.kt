import org.gradle.api.JavaVersion

@Suppress("ConstPropertyName")
object ProjectConfig {
    const val packageName = "com.dora.user"

    const val minSdk = 26
    const val compileSdk = 36
    const val targetSdk = 36
    const val versionCode = 1
    const val versionName = "1.0.0"

    const val IS_DEBUG = true

    val javaVersion = JavaVersion.VERSION_23
    const val BASE_URL_LIVE = ""
    const val BASE_URL_DEV = ""
}