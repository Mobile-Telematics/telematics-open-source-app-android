@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
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

        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://s3.us-east-2.amazonaws.com/android.telematics.sdk.production/") }
        maven { url = uri("https://muxinc.jfrog.io/artifactory/default-maven-release-local") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
rootProject.name = "TelematicsApp"

include(
    ":app",
    ":features:account",
    ":core:common",
    ":core:content",
    ":core:data",
    ":core:database",
    ":core:datastore",
    ":core:domain",
    ":core:encryption",
    ":core:logger",
    ":core:model",
    ":core:network",
    ":features:dashboard",
    ":features:feed",
    ":features:leaderboard",
    ":features:login",
    ":features:obd",
    ":features:onboarding",
    ":features:reward",
    ":features:settings",
    ":features:splash",
)


