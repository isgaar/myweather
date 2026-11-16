// Location: settings.gradle.kts
// Function: Gradle settings configuring repository mirrors and root module
// inclusion for the zero-telemetry MyWeather Android project.
// All Rights Reserved isgaar

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "myweather"
include(":app")

