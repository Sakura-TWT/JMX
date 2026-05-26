// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.application) apply false
    id("com.android.library") version "9.1.0" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    id("com.vanniktech.maven.publish") version "0.36.0" apply false
}
