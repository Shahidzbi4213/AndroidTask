plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")

}

android {
    namespace = "com.gulehri.androidtask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gulehri.androidtask"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += listOf("en")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    applicationVariants.all {
        outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName =
                    "Shahsaud Task ${this.baseName} - ${this.versionName} - ${this.versionCode}.apk"
                output.outputFileName = outputFileName
            }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {

            resValue("string", "admob_app_id", "ca-app-pub-3940256099942544~3347511713")

            //Interstitial
            resValue("string", "interstitial", "ca-app-pub-3940256099942544/1033173712")

            //Native
            resValue("string", "natives", "ca-app-pub-3940256099942544/2247696110")

            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        android.buildFeatures.apply {
            viewBinding = true
            buildConfig = true
        }

    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val navVersion = "2.7.6"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    //Firebase
    implementation("com.google.firebase:firebase-common-ktx:20.4.2")


    //Screen Responsiveness
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    //Admob
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    //PermissionX
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")

    //coil
    implementation("io.coil-kt:coil:2.5.0")

    //Swipe Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    //shimmer
    api("com.facebook.shimmer:shimmer:0.5.0")



}