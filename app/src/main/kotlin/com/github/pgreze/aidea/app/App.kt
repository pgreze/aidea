package com.github.pgreze.aidea.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.file
import com.github.pgreze.aidea.idea.listIdeaInstallations
import java.io.File

fun main(args: Array<String>) {
    App().main(args)
}

class App : CliktCommand() {

    private val target: File? by argument(help = "File or folder to open an IDEA based IDE with.")
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
                TODO("Open an IDEA instance with this project")
            }
            target.isFile && target.endsWith(".main.kts") -> {
                TODO("Generate and open a KTS friendly project")
            }
            else -> {
                error("Unsupported file: $target")
            }
        }
    }
}
