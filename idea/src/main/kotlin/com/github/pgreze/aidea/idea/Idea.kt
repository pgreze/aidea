package com.github.pgreze.aidea.idea

import java.io.File
import java.lang.RuntimeException

data class IdeaInstall(
    val installDir: File,
    val launcher: File,
    val ideaType: IdeaType,
    val infoString: String?,
)

enum class IdeaType(val launcherName: String) {
    IDEA("idea"),
    ANDROID_STUDIO("studio"),
}

fun listIdeaInstallations(): Sequence<IdeaInstall> = sequence {
    val userHome = System.getProperty("user.home")
        ?.let(::File)
        ?: throw RuntimeException("Property user.home not found")

    // Iterate all Jetbrains Toolbox installations
    val toolboxRootDir = userHome.resolve(
        "Library/Application Support/JetBrains/Toolbox/apps"
    )
    yieldAll(
        toolboxRootDir.resolve("IDEA-C")
            .resolveToolboxLaunchers(IdeaType.IDEA)
    )
    yieldAll(
        toolboxRootDir.resolve("AndroidStudio")
            .resolveToolboxLaunchers(IdeaType.ANDROID_STUDIO)
    )

    // Iterate the system + user Applications folders
    listOf(
        File("/Applications"),
        userHome.resolve("Applications"),
    ).flatMap { it.listFiles()?.toList() ?: listOf() }.forEach { application ->
        when {
            application.extension != "app" -> null
            // brew install intellij-idea{,-ce}
            application.name.startsWith("IntelliJ IDEA") ->
                application.resolveLauncher(IdeaType.IDEA)
            // brew install android-studio{,-preview-beta,-preview-canary}
            application.name.startsWith("Android Studio") ->
                application.resolveLauncher(IdeaType.ANDROID_STUDIO)
            else -> null
        }?.let { yield(it) }
    }
}

private fun File.resolveToolboxLaunchers(
    ideaType: IdeaType
): Sequence<IdeaInstall> = sequence {
    listFiles()?.forEach f1@{ f1 -> // ch-0
        if (f1.isDirectory.not()) return@f1
        f1.listFiles()?.forEach f2@{ f2 -> // 202.7660.26
            if (f2.isDirectory.not()) return@f2
            f2.listFiles()?.forEach f3@{ f3 ->
                yield(f3?.resolveLauncher(ideaType) ?: return@f3)
            }
        }
    }
}

internal fun File.resolveLauncher(
    ideaType: IdeaType,
): IdeaInstall? {
    return IdeaInstall(
        installDir = this
            .takeIf(File::isDirectory)
            ?: return null,
        launcher = resolve("Contents/MacOS/${ideaType.launcherName}")
            .takeIf(File::isFile)
            ?: return null,
        ideaType = ideaType,
        infoString = resolve("Contents/Info.plist")
            .takeIf(File::exists)
            ?.useLines { lines ->
                lines.dropWhile { it.trim() != "<key>CFBundleGetInfoString</key>" }
                    .drop(1) // Drop the above header
                    .first()
                    .extractInfoString()
            }
    )
}

/**
 * Drop the <string> XML indicators and the copyright line,
 * e.g '. Copyright JetBrains s.r.o., (c) 2000-2022'.
 */
internal fun String.extractInfoString(): String = trim().let {
    // Alternative: we could use the ". " separator
    it.substring("<string>".length, it.indexOf("Copyright") - 2)
}
