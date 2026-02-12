plugins {
    id("ggomodoro.android.library")
    id("ggomodoro.hilt")
    alias(libs.plugins.room)
}

android {
    namespace = "com.ggomodoro.core.database"
    
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
