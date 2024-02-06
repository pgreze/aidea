package com.github.pgreze.aidea.idea

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IdeaTest {
    @Test
    fun extractInfoString() {
        val infoString = "IntelliJ IDEA 2022.2.3, build IC-222.4345.14"
        val copyright = "Copyright JetBrains s.r.o., (c) 2000-2022"
        val string = "  <string>$infoString. $copyright</string>"

        string.extractInfoString() shouldBe infoString
    }
}
