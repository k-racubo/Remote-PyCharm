plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.intellijPlatform)
    alias(libs.plugins.ksp)
}

group = "com.kracubo"
version = "1.0.0"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(project(":api"))

    implementation(libs.ktorCore)
    implementation(libs.ktorCio)
    implementation(libs.ktorWebsockets)
    implementation(libs.ktorContentNegotation)
    implementation(libs.ktorSerialization)
    implementation(libs.ktorCors)

    implementation(libs.kotlinxSerialization)

    implementation(libs.mDnsCore)

    implementation(libs.autoServiceAnnotations)
    ksp(libs.autoServiceKsp)

    configurations.all {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
    }

    compileOnly(libs.kotlinxCoroutines)

    intellijPlatform {
        create("PC", "2025.2")

        bundledPlugin("PythonCore")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "232"
            untilBuild = "252.*"
        }
    }
}