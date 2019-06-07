package com.carbonblack.intellij.rpmmacro

import com.intellij.testFramework.ParsingTestCase

class RpmMacroParsingTest : ParsingTestCase("", "rpmmacros", RpmMacroParserDefinition()) {

    fun testParsingTestData() {
        doTest(true)
    }

    override fun getTestDataPath(): String = "src/test/resources/macros"

    override fun skipSpaces(): Boolean = false

    override fun includeRanges(): Boolean = true
}
