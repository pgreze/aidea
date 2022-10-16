plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("com.github.pgreze.aidea.app.AppKt")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("aidea")
}

dependencies {
    implementation(project(":idea"))

    api(KotlinX.coroutines.core)
    implementation("com.github.ajalt.clikt:clikt:_")
    implementation("com.github.pgreze:kotlin-process:_")
}
