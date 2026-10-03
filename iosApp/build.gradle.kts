plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Compox2DiOS"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.compose)
            implementation(projects.samples)
        }
    }
}
