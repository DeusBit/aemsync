import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
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
    maven
    `maven-publish`
}

repositories {
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

    compile("org.asynchttpclient:async-http-client:$asynchttpclientVersion")
    compile("com.google.code.gson:gson:$gsonVersion")

    testCompile(project(":aemsync-test-framework"))
    testCompile("org.jmockit:jmockit:$jmockitVersion")
    testCompile("org.assertj:assertj-core:$assertjVersion")
    testCompile("org.mockito:mockito-core:$mockitoVersion")
    testCompile("com.nhaarman:mockito-kotlin:$mockitoKotlinVersion")

    testCompile("org.jetbrains.spek:spek-api:$spekVersion")
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
    testCompile("org.jetbrains.spek:spek-subject-extension:$spekVersion")
    testCompile("org.junit.jupiter:junit-jupiter-api:$junitJupiterApiVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testRuntime("org.junit.vintage:junit-vintage-engine:$junitVintageEngineVersion")

    testCompile("junit", "junit", junitVersion)
    testCompile("org.mock-server:mockserver-netty:$mockserverVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configure<KotlinProjectExtension> {
    experimental.coroutines = Coroutines.ENABLE
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn += "classes"

    classifier = "sources"
    from(the<JavaPluginConvention>().sourceSets["main"].allSource)
}

artifacts {
    add("archives", sourcesJar)
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("AemSyncPublication") {
        from(components["java"])
        artifact(sourcesJar)

        groupId = "deeprim"
        artifactId = "aemsync"
        version = "0.1"

        pom.withXml {
            asNode().apply {
                appendNode("description", "Aem sync project")
                appendNode("name", "selenium-runner")
                appendNode("url", "https://github.com/deeprim/aemsync")

                val license = appendNode("licenses").appendNode("license")
                license.appendNode("name", "The Apache Software License, Version 2.0")
                license.appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                license.appendNode("distribution", "repo")

                val developer = appendNode("developers").appendNode("developer")
                developer.appendNode("id", "Dmytriy Primshyts")
                developer.appendNode("name", "Dmytriy Primshyts")
                developer.appendNode("email", "dprimshyts@gmail.com")

                appendNode("scm").appendNode("url", "https://github.com/deeprim/aemsync")
            }
        }
    }
}
