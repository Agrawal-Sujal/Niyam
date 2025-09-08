// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.57" apply false
    id("com.diffplug.spotless") version "7.2.1"
}
subprojects {
    apply(plugin = "com.diffplug.spotless")

    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            trimTrailingWhitespace()
            ktlint("0.50.0")
            targetExclude("build/**", "**/generated/**") // Exclude build and generated files
            leadingTabsToSpaces()
            endWithNewline()
        }

        kotlinGradle {
            target("**/*.gradle.kts")
            ktlint()
        }


    }
}
