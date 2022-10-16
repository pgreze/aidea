package com.github.pgreze.aidea.app

import com.github.pgreze.aidea.idea.IdeaInstall

fun List<IdeaInstall>.displayChoiceHeaders() {
    withIndex().forEach { (index, install) ->
        val prefix = (index + 1).toString().padStart(3, ' ')
        println("$prefix: ${install.infoString}")
        println( " ".repeat(prefix.length + 2) + install.installDir)
        println()
    }
}

fun selectInstallIndex(size: Int): Int {
    while (true) {
        print("Which installation to use? ")
        return (readLine() ?: return -1) // ctrl+d
            .toIntOrNull()
            ?.takeIf { it - 1 in 0 until size }
            ?: continue
    }
}
