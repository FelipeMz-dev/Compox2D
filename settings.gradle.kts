rootProject.name = "Compox2D"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val isJitPackBuild = providers.environmentVariable("JITPACK").orNull != null

if (isJitPackBuild) {
    include(":core", ":compose")
} else {
    // Local development modules: app entry points and platform demos.
    include(":androidApp")
    include(":desktopApp")
    include(":webApp")
    include(":iosApp")

    // Engine core and optional Compose integration.
    include(":core", ":compose")

    // Non-published examples and demos.
    include(":samples")
}