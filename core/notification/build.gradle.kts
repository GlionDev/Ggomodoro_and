plugins {
    id("ggomodoro.android.library")
    id("ggomodoro.hilt")
}

android {
    namespace = "com.ggomodoro.core.notification"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
