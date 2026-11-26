import java.lang.Boolean.TRUE

plugins {
    alias(libs.plugins.protobuf)
}

// J2V8 is only published on JitPack. The repositories are listed explicitly because declaring a repository
// in a project makes Gradle ignore the ones inherited from the settings (PREFER_PROJECT).
repositories {
    google()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.ynab")
        }
    }
}

android {
    namespace = "app.revanced.rvx.extension"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = TRUE
            // Module specific rules on top of the global extensions/proguard-rules.pro.
            proguardFiles("proguard-rules.pro")

            // 'libj2v8.so' is already included in the patch.
            ndk {
                abiFilters.add("")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly(libs.annotation)
    compileOnly(libs.preference)

    implementation(libs.collections4)
    implementation(libs.gson)
    implementation(libs.lang3)
    implementation(libs.okhttp3)
    implementation(libs.protobuf.javalite)

    //noinspection UseTomlInstead
    implementation("com.github.ynab:J2V8:6.2.1-16kb.2@aar")

    coreLibraryDesugaring(libs.desugar.jdk.libs)
    compileOnly(project(":extensions:rvx-shared:stub"))
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
