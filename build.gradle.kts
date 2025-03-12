
    plugins {
        alias(libs.plugins.android.application) apply false
        alias(libs.plugins.kotlin.android) apply false
        id("com.google.gms.google-services") version "4.4.2" apply false
        id("org.jetbrains.kotlin.jvm") version "2.1.0" apply false
        id("androidx.navigation.safeargs.kotlin") version "2.7.2" apply false
    }


