package com.github.pgreze.aidea.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.file
import com.github.pgreze.aidea.idea.IdeaInstall
import com.github.pgreze.aidea.idea.IdeaType
import com.github.pgreze.aidea.idea.listIdeaInstallations
import com.github.pgreze.process.process
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    App().main(args)
}

class App : CliktCommand(
    help = """
    idea alternative supporting a multi-installation setup
    (like Jetbrains Toolbox but in your shell).

    Without any argument, displays all the found installations.

    Provides a folder to open it with the selected IDE.

    Provides a .main.kts file to open it in a temporary IDEA session.
""".trimIndent()
) {
    private val target: File? by argument(help = "File or folder to interact with.")
        .file(mustExist = true)
        .optional()

    private val ideaInstalls by lazy {
        listIdeaInstallations().sortedByDescending { it.installDir }
    }

    override fun run() {
        val target = target ?: run {
            // Display the installation paths.
            ideaInstalls.forEach { println(it.installDir) }
            return
        }

        when {
            target.isDirectory -> {
                openProject(target)
                    .let(::exitProcess)
            }

            target.isFile && target.name.endsWith(".main.kts") -> {
                openMainKtsFile(target)
                    .let(::exitProcess)
            }

            else -> {
                failWithMessage("Unsupported file: $target")
            }
        }
    }

    private fun openProject(target: File): Int = runBlocking {
        val ideaInstalls = ideaInstalls.toNonEmptyList()

        val selectedInstall = ideaInstalls.chooseInstall()
            ?: return@runBlocking 0

        selectedInstall.openProject(target)
    }

    private fun openMainKtsFile(target: File): Int = runBlocking {
        val projectDir = target.generateMainKtsProject()

        val ideaInstalls = ideaInstalls
            .filter { it.ideaType == IdeaType.IDEA }
            .toNonEmptyList()

        val selectedInstall = ideaInstalls.chooseInstall()
            ?: return@runBlocking 0

        selectedInstall.openProject(projectDir)
    }
}

fun Sequence<IdeaInstall>.toNonEmptyList(): List<IdeaInstall> =
    toList()
        .takeUnless { it.isEmpty() }
        ?: failWithMessage("No IDEA installation found")

fun failWithMessage(message: String): Nothing {
    System.err.println(message)
    exitProcess(1)
}

suspend fun IdeaInstall.openProject(target: File): Int =
    process(
        "/usr/bin/open",
        "-na",
        launcher.absolutePath.toString(),
        "--args",
        target.absolutePath.toString(),
    ).resultCode
