plugins {
    kotlin("jvm") version "2.2.0"
}

group = "io.github.bradenk04"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}