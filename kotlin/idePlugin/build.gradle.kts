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
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(project(":api"))
    implementation(libs.mDnsCore)
    implementation(libs.kotlinxSerialization)

    implementation(libs.autoServiceAnnotations)
    ksp(libs.autoServiceKsp)

    intellijPlatform {
        pycharmCommunity("2025.2")
        bundledPlugin("PythonCore")
    }

    implementation(libs.ktorCore)
    implementation(libs.ktorCio)
    implementation(libs.ktorWebsockets)
    implementation(libs.ktorContentNegotation)
    implementation(libs.ktorSerialization)
    implementation(libs.ktorCors)

    compileOnly(libs.kotlinxCoroutines)
}

configurations.all {
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = provider { null }
        }
    }
}