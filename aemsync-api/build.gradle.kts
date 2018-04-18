import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.aemsync"
version = "0.1"

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
}

val kotlinVersion: String by extra
val junitVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    compile(kotlin("stdlib-jdk8", kotlinVersion))

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    testCompile("junit", "junit", junitVersion)
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
