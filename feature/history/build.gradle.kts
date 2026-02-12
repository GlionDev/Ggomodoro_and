plugins {
    id("ggomodoro.android.library")
    id("ggomodoro.hilt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ggomodoro.feature.history"
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
