package com.github.pgreze.aidea.app

import com.github.pgreze.aidea.idea.IdeaInstall

fun List<IdeaInstall>.chooseInstall(): IdeaInstall? {
    if (size == 1) return first()
    displayChoiceHeaders()
    return selectInstallIndex(size)
        ?.let(::get)
}

private fun List<IdeaInstall>.displayChoiceHeaders() {
    withIndex().forEach { (index, install) ->
        val prefix = (index + 1).toString().padStart(3, ' ')
        println("$prefix: ${install.infoString}")
        println( " ".repeat(prefix.length + 2) + install.installDir)
        println()
    }
}

private fun selectInstallIndex(size: Int): Int? {
    while (true) {
        print("Which installation to use? ")
        return (readlnOrNull() ?: return null) // ctrl+d
            .toIntOrNull()
            ?.takeIf { it - 1 in 0 until size }
            ?.let { it - 1 }
            ?: continue
    }
}
