import com.android.build.api.variant.ApplicationAndroidComponentsExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

val androidComponents = extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
androidComponents.onVariants { variant ->
    if (variant.name == "debug") {
        tasks.register<JavaExec>("com.example.myapplication.MainKt.main()") {
            group = "application"
            mainClass.set("com.example.myapplication.MainKt")
            classpath = files(
                layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes"),
                layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"),
                layout.buildDirectory.dir("tmp/kotlin-classes/debug")
            )
            dependsOn("assembleDebug")
        }
        tasks.register<JavaExec>("runMain") {
            group = "application"
            mainClass.set("com.example.myapplication.MainKt")
            classpath = files(
                layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes"),
                layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"),
                layout.buildDirectory.dir("tmp/kotlin-classes/debug")
            )
            dependsOn("assembleDebug")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
