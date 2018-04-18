import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion: String by extra

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

group = "org.aemsync"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin")
}

plugins {
    java
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

val kotlinVersion: String by extra
val kotlinCoroutinesVersion: String by extra
val gsonVersion: String by extra
val junitVersion: String by extra
val asynchttpclientVersion: String by extra
val mockserverVersion: String by extra
val jmockitVersion: String by extra
val assertjVersion: String by extra
val mockitoVersion: String by extra
val mockitoKotlinVersion: String by extra
val spekVersion: String by extra
val junitJupiterApiVersion: String by extra
val junitVintageEngineVersion: String by extra
val junitJupiterEngineVersion: String by extra

dependencies {
    compile(kotlin("stdlib-jdk8", kotlinVersion))

    compile(project(":aemsync-api"))

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    compile("org.jmockit:jmockit:$jmockitVersion")
    compile("org.assertj:assertj-core:$assertjVersion")
    compile("org.mockito:mockito-core:$mockitoVersion")
    compile("com.nhaarman:mockito-kotlin:$mockitoKotlinVersion")

    compile("org.jetbrains.spek:spek-api:$spekVersion")
    compile("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
    compile("org.jetbrains.spek:spek-subject-extension:$spekVersion")
    compile("org.junit.jupiter:junit-jupiter-api:$junitJupiterApiVersion")
    compile("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    compile("org.junit.vintage:junit-vintage-engine:$junitVintageEngineVersion")

    compile("junit", "junit", junitVersion)
    compile("org.mock-server:mockserver-netty:$mockserverVersion")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
