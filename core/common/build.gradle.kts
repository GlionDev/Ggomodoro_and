plugins {
    id("ggomodoro.android.library")
    id("ggomodoro.hilt")
}

android {
    namespace = "com.ggomodoro.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
