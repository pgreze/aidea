package com.github.pgreze.aidea.app

import com.github.pgreze.process.process
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.io.path.createSymbolicLinkPointingTo
import kotlin.math.abs
import kotlin.system.exitProcess

fun File.openMainKtsFile(): String? = runBlocking {
    val identifier = absolutePath.hashCode().let(::abs).toString()
    val projectDir = File(System.getProperty("user.home"))
        .resolve(".kidea/$identifier-$name")

    require(projectDir.deleteRecursively()) {
        return@runBlocking "Could not delete $projectDir"
    }
    projectDir.mkdirs()
    projectDir.resolve("build.gradle.kts")
        .writeText(BUILD_GRADLE_KTS)
    projectDir.resolve("settings.gradle")
        .writeText("rootProject.name = \"$name\"")

    projectDir.resolve("src").let { srcDir ->
        srcDir.mkdir()
        srcDir.resolve(name).toPath()
            .createSymbolicLinkPointingTo(toPath().toAbsolutePath())
    }

    process("idea", projectDir.absolutePath)
    null
}

private val BUILD_GRADLE_KTS = """
    plugins {
        kotlin("jvm") version "1.7.10"
    }

    repositories {
        mavenCentral { content { includeGroupByRegex("org.jetbrains(|.kotlin)") } }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
    }
""".trimIndent()
