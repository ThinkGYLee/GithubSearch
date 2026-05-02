pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GithubSearch"
include(":app")
include(":feature:detail")
include(":feature:home")
include(":feature:setting")
include(":feature:favorite")
include(":core")
include(":data")
include(":domain")
include(":build-logic")
