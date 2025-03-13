plugins {
    kotlin("jvm") version "2.1.10"
    id("com.diffplug.spotless") version "6.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(22)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("0.41.0")
    }
}