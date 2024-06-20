pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
//        maven(url = "https://github.com/omadahealth/omada-nexus/raw/master/release")
        maven(url = "https://jitpack.io")
//        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://github.com/omadahealth/omada-nexus/raw/master/release")
        maven(url = "https://jitpack.io")
        jcenter()
        google()
        mavenCentral()
    }
}

rootProject.name = "CashTracker"
include(":app")