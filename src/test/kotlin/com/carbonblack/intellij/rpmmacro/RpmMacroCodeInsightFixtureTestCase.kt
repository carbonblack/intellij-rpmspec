package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class RpmMacroCodeInsightFixtureTestCase : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String = "src/test/resources/macros"

    fun testReference() {
        myFixture.configureByFiles("ReferenceTestData.spec", "ParsingTestData.rpmmacros")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent.node.findChildByType(RpmSpecTypes.MACRO)!!.psi
        assertEquals("/usr/bin/bzip2", (element.references[0].resolve() as RpmMacroMacro).macroBody?.text)
    }
}
