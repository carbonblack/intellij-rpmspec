package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

class RpmSpecCodeInsightFixtureTestCase : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/resources/spec"

    fun testCompletion() {
        myFixture.configureByFiles("CompletionTestData.spec")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        Assert.assertTrue(strings!!.containsAll(listOf("pybasever", "pyshortver")))
    }

    fun testAnnotator() {
        myFixture.configureByFiles("ParsingTestData.spec")
        myFixture.checkHighlighting(true, true, true, true)
    }

    fun testFolding() {
        myFixture.testFolding("$testDataPath/FoldingTestData.spec")
    }

    fun testFindUsages() {
        val usageInfos = myFixture.testFindUsages("FindUsagesTestData.spec")
        Assert.assertEquals(3, usageInfos.size)
    }

    fun testCommenter() {
        myFixture.configureByText("TempSpecFile.spec", "<caret>%global my_macro 3")
        val commentAction = CommentByLineCommentAction()
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("#%global my_macro 3")
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("%global my_macro 3")
    }

    fun testReference() {
        myFixture.configureByFiles("ReferenceTestData.spec")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent.node.findChildByType(RpmSpecTypes.MACRO)!!.psi
        assertEquals("%global upstream_version %{general_version}%{?prerel}", element.reference?.resolve()?.text)
    }
}
