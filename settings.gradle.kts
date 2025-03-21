pluginManagement {
    repositories {
        maven("https://google403.ir/")
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://google403.ir/")
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "learn_en"
include(":app")
