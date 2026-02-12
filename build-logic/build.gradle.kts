plugins {
    `kotlin-dsl`
}

group = "com.ggomodoro.buildlogic"

// Need to resolve these dependencies first
dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.room.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
    implementation(libs.androidx.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "ggomodoro.android.application"
            implementationClass = "com.ggomodoro.app.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "ggomodoro.android.library"
            implementationClass = "com.ggomodoro.app.AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "ggomodoro.hilt"
            implementationClass = "com.ggomodoro.app.AndroidHiltConventionPlugin"
        }
    }
}
