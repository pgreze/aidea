plugins {
    application
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.palantir.graal") version "0.12.0"
}

application {
    mainClass.set("com.github.pgreze.aidea.app.AppKt")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("aidea")
}

// https://github.com/palantir/gradle-graal
// :nativeImage is building a bigger but standalone executable
configure<com.palantir.gradle.graal.GraalExtension> {
    mainClass(application.mainClass.get())
    outputName(rootProject.name)
    javaVersion("11")
    option("--report-unsupported-elements-at-runtime")
    option("--initialize-at-build-time")
    option("--no-fallback")
    option("--no-server")
}

dependencies {
    implementation(project(":idea"))

    api(KotlinX.coroutines.core)
    implementation("com.github.ajalt.clikt:clikt:_")
    implementation("com.github.pgreze:kotlin-process:_")
}
