package com.carbonblack.intellij.rpmspec.shell

import com.intellij.lexer.Lexer
import com.intellij.testFramework.LexerTestCase

class RpmSpecShellLexerTest : LexerTestCase() {

    fun testPython3Spec() {
        doFileTest("spec")
    }

    fun testParsingTestData() {
        doFileTest("spec")
    }

    override fun createLexer(): Lexer = RpmSpecShellLexerAdapter()
    override fun getDirPath(): String = "src/test/resources/macros/shell"

    override fun getPathToTestDataFile(extension: String?): String {
        val filename = "/spec/shell/${getTestName(true)}$extension"
        return checkNotNull(javaClass.getResource(filename)?.path) { "Missing file $filename" }
    }
}
