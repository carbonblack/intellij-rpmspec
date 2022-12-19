package com.carbonblack.intellij.rpmspec

import com.intellij.testFramework.ParsingTestCase

class RpmSpecParsingTest : ParsingTestCase("", "spec", RpmSpecParserDefinition()) {

    fun testParsingTestData() {
        doTest(true)
    }

    fun testAutoSpecTestData() {
        doTest(true)
    }

    override fun getTestDataPath(): String = "src/test/resources/spec"

    override fun skipSpaces(): Boolean = false

    override fun includeRanges(): Boolean = true
}
