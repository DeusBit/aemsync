import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.aemsync"
version = "1.0-SNAPSHOT"

buildscript {
    val kotlinVersion: String by extra

    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

apply {
    plugin("kotlin")
}

plugins {
    java
}

val kotlinVersion: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8", kotlinVersion))
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

task<Wrapper>("gradleWrapper") {
    gradleVersion = "4.6"
    distributionType = Wrapper.DistributionType.ALL
}

