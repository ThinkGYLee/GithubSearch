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
