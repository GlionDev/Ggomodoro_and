plugins {
    id("ggomodoro.android.library")
    id("ggomodoro.hilt")
}

android {
    namespace = "com.ggomodoro.domain"
}

dependencies {
    implementation(libs.javax.inject)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
