plugins {
    kotlin("jvm") version "1.9.0"
}

group = "com.zhools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // JSON
    implementation ("com.google.code.gson:gson:2.10.1")

    // Chinese word segmentation
    implementation ("com.hankcs:hanlp:portable-1.8.4")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // HTTP Network request
    implementation ("org.jsoup:jsoup:1.16.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}