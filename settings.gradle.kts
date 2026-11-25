rootProject.name = "revanced-patches"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        maven {
            name = "githubPackages"
            url = uri("https://maven.pkg.github.com/revanced/revanced-patches-gradle-plugin")
            // GitHub Packages needs a token that can read packages of *another* owner (revanced).
            // The default GITHUB_TOKEN of a fork usually cannot, so a classic PAT with `read:packages` is preferred:
            //   - CI:    ORG_GRADLE_PROJECT_githubPackagesUsername / ORG_GRADLE_PROJECT_githubPackagesPassword
            //   - local: githubPackagesUsername / githubPackagesPassword in ~/.gradle/gradle.properties
            credentials {
                username = providers.gradleProperty("githubPackagesUsername")
                    .orElse(providers.gradleProperty("gpr.user"))
                    .orElse(providers.environmentVariable("GITHUB_ACTOR"))
                    .orNull
                password = providers.gradleProperty("githubPackagesPassword")
                    .orElse(providers.gradleProperty("gpr.key"))
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                    .orNull
            }
        }
        // TODO: Remove once https://github.com/google/protobuf-gradle-plugin/pull/797 is merged.
        maven { url = uri("https://jitpack.io") }
    }
    // TODO: Remove once https://github.com/google/protobuf-gradle-plugin/pull/797 is merged.
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.protobuf") {
                useModule("com.github.ReVanced:protobuf-gradle-plugin:${requested.version}")
            }
        }
    }
}

plugins {
    id("app.revanced.patches") version "1.0.0-dev.10"
}

settings {
    extensions {
        defaultNamespace = "app.revanced.extension"

        // Must resolve to an absolute path (not relative),
        // otherwise the extensions in subfolders will fail to find the proguard config.
        proguardFiles(rootProject.projectDir.resolve("extensions/proguard-rules.pro").toString())
    }
}
