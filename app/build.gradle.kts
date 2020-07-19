/*apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'
apply plugin: 'realm-android'*/

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("realm-android")
}

kapt {
    correctErrorTypes = true
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        applicationId = "com.appetiserapps.itunessearch"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    dataBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    testOptions {
        unitTests(delegateClosureOf<com.android.build.gradle.internal.dsl.TestOptions.UnitTestOptions> {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        })
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Libs.kotlin_stdlib_jdk7)
    implementation(Libs.appcompat)
    implementation(Libs.core_ktx)
    implementation(Libs.constraintLayout)
    implementation(Libs.coordinatorLayout)
    implementation(Libs.androidxLegacySupportV4)
    implementation(Libs.googleMaterial)

    // testing dependencies
    testImplementation(Libs.junit_unit_test)
    // Optional -- Robolectric environment
    testImplementation(Libs.androidx_test_core)
    testImplementation(Libs.robolectric)
    // Optional -- Mockito framework
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.mockitoInline)
    testImplementation(Libs.mockitoKotlin)
    // Coroutines test
    testImplementation(Libs.coroutines_test)
    // Core library
    androidTestImplementation(Libs.androidx_test_core)
    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation(Libs.testRunner)
    androidTestImplementation(Libs.testRules)
    // Assertions
    androidTestImplementation(Libs.androidx_junit_ext)
    androidTestImplementation(Libs.testTruth)
    androidTestImplementation(Libs.googleTruth)
    testImplementation(Libs.googleTruth)
    // Espresso dependencies
    androidTestImplementation(Libs.espresso_core)

    kapt(Libs.epoxy_annotation_processor)

    kapt(Libs.glide_compiler)

    // Coding Challenge Library (self-made library)
    implementation(Libs.adrCodingChallenge)
    /*implementation 'com.github.Nei1eveN:ADR-Coding-Challenge:test-dependencies-alpha-v1'*/
}
