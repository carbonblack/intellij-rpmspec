package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmspec.RpmSpecSettingsState
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.*
import java.io.File
import java.io.IOException

class RpmMacroUtilTest : BasePlatformTestCase() {
    fun testMacroPathDetection() {
        clearAllMocks()

        val outputText = File("src/test/resources/macros/RpmShowRcOutput.txt")

        val mockProcess = mockk<Process>(relaxed = true)
        every { mockProcess.inputStream } returns outputText.inputStream()
        every { mockProcess.waitFor(any(), any()) } returns true

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockProcess

        expectThat(RpmMacroUtil.getRpmMacroPaths())
            .isNotEmpty()
            .containsExactlyInAnyOrder(
                "/usr/lib/rpm/macros",
                "/usr/lib/rpm/macros.d/macros.*",
                "/usr/lib/rpm/platform/x86_64-linux/macros",
                "/usr/lib/rpm/fileattrs/*.attr",
                "/usr/lib/rpm/suse/macros",
                "/etc/rpm/macros.*",
                "/etc/rpm/macros",
                "/etc/rpm/x86_64-linux/macros",
                "~/.rpmmacros"
            )
    }

    fun testMacroPathDetectionDarwin() {
        clearAllMocks()

        val outputText = File("src/test/resources/macros/RpmShowRcOutputDarwin.txt")

        val mockProcess = mockk<Process>(relaxed = true)
        every { mockProcess.inputStream } returns outputText.inputStream()
        every { mockProcess.waitFor(any(), any()) } returns true

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockProcess

        expectThat(RpmMacroUtil.getRpmMacroPaths())
            .isNotEmpty()
            .containsExactlyInAnyOrder(
                "/usr/local/Cellar/rpm/4.16.1.2/lib/rpm/macros",
                "/usr/local/Cellar/rpm/4.16.1.2/lib/rpm/macros.d/macros.*",
                "/usr/local/Cellar/rpm/4.16.1.2/lib/rpm/platform/x86_64-darwin/macros",
                "/usr/local/Cellar/rpm/4.16.1.2/lib/rpm/fileattrs/*.attr",
                "/usr/local/Cellar/rpm/4.16.1.2/lib/rpm/homebrew/macros",
                "/usr/local/etc/rpm/macros.*",
                "/usr/local/etc/rpm/macros",
                "/usr/local/etc/rpm/x86_64-darwin/macros",
                "~/.rpmmacros"
            )
    }

    fun testMissingRpmCommand() {
        clearAllMocks()

        mockkConstructor(ProcessBuilder::class)
        every {
            anyConstructed<ProcessBuilder>().command(listOf(RpmSpecSettingsState.instance.rpmCommandPath, "--showrc"))
        } answers {
            (callOriginal()).command("unknown-command")
        }

        expectCatching { RpmMacroUtil.getRpmMacroPaths() }
            .isFailure()
            .isA<IOException>()
            .get { message!! }.contains("No such file or directory")
    }

    fun testRpmCommandTimeout() {
        clearAllMocks()

        val outputText = File("src/test/resources/macros/RpmShowRcOutput.txt")

        val mockProcess = mockk<Process>(relaxed = true)
        every { mockProcess.inputStream } returns outputText.inputStream()
        every { mockProcess.waitFor(any(), any()) } returns false

        mockkConstructor(ProcessBuilder::class)
        every { anyConstructed<ProcessBuilder>().start() } returns mockProcess

        expectCatching { RpmMacroUtil.getRpmMacroPaths() }
            .isFailure()
            .isA<Exception>()
            .get { message!! }.contains("cancelled")
    }
}