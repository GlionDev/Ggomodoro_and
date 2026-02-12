plugins {
    id("ggomodoro.android.library")
    id("ggomodoro.hilt")
}

android {
    namespace = "com.ggomodoro.data"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    
    implementation(project(":domain"))
    implementation(project(":core:database"))
}
