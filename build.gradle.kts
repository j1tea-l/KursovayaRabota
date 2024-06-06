// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath ("io.realm:realm-gradle-plugin:10.17.0")
    }
}