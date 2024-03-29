package com.github.pgreze.aidea.app

import java.io.File
import java.io.IOException
import kotlin.io.path.createSymbolicLinkPointingTo
import kotlin.math.absoluteValue

fun File.resolveMainKtsProject(): File {
    val identifier = absolutePath.hashCode().absoluteValue.toString()
    return File(System.getProperty("user.home"))
        .resolve(".aidea/kts/$identifier-$name")
}

fun File.generateMainKtsProject(): File =
    resolveMainKtsProject()
        .apply {
            require(deleteRecursively()) {
                throw IOException("Could not delete everything in $this")
            }
            mkdirs()

            resolve("build.gradle.kts").writeText(BUILD_GRADLE_KTS)

            resolve("settings.gradle").writeText("rootProject.name = \"${name}\"")

            resolve("src").let { srcDir ->
                srcDir.mkdir()
                srcDir.resolve(this@generateMainKtsProject.name).toPath()
                    .createSymbolicLinkPointingTo(this@generateMainKtsProject.toPath().toAbsolutePath())
            }
        }

private const val BUILD_GRADLE_KTS = """plugins {
    kotlin("jvm") version "1.8.20"
}
"""
