package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class RpmMacroCodeInsightFixtureTestCase : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String = "src/test/resources/macros"

    fun testReference() {
        if (false) {
            myFixture.configureByFiles("ReferenceTestData.java", "DefaultTestData.rpmmacros")
            val element = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent
            assertEquals("http://en.wikipedia.org/", (element.references[0].resolve() as RpmMacroMacro).text)
        } else {
            myFixture.configureByFiles("ReferenceTestData.spec", "DefaultTestData.rpmmacros")
            val element = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent.node.findChildByType(RpmSpecTypes.MACRO)!!.psi
            assertEquals("/usr/bin/bzip2", (element.references[0].resolve() as RpmMacroMacro).text)
        }

        //val element = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent
        //assertEquals("_bi", element.node.findChildByType(RpmMacroTypes.MACRO)!!.text)
    }
}
