package com.github.pgreze.aidea.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.file
import com.github.pgreze.aidea.idea.IdeaInstall
import com.github.pgreze.aidea.idea.listIdeaInstallations
import com.github.pgreze.process.process
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    App().main(args)
}

class App : CliktCommand(help = """
    idea alternative supporting a multi-installation setup
    (like Jetbrains Toolbox but in your shell).

    Without any argument, displays all the found installations.

    Provides a folder to open it with the selected IDE.

    Provides a .main.kts file to open it in a temporary IDEA session.
""".trimIndent()) {

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
                target.openMainKtsFile()
                    ?.let(::failWithMessage)
            }
            else -> {
                failWithMessage("Unsupported file: $target")
            }
        }
    }

    private fun openProject(target: File): Int = runBlocking {
        val ideaInstalls = ideaInstalls.toList()
            .ensureNotEmpty()

        ideaInstalls.displayChoiceHeaders()

        val selectedInstallIndex = selectInstallIndex(ideaInstalls.size)
        if (selectedInstallIndex == -1) return@runBlocking 0
        val selectedLauncher = ideaInstalls[selectedInstallIndex]
            .launcher
            .toString()

        process(
            "/usr/bin/open",
            "-na",
            selectedLauncher,
            "--args",
            target.absolutePath.toString(),
        ).resultCode
    }

    private fun List<IdeaInstall>.ensureNotEmpty(): List<IdeaInstall> =
        this.takeUnless { it.isEmpty() }
            ?: failWithMessage("No IDEA installation found")

    private fun failWithMessage(message: String): Nothing {
        echo(message, err = true)
        exitProcess(1)
    }
}
