package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.junit.Assert

import java.util.Arrays

class RpmSpecCodeInsightFixtureTestCase : LightCodeInsightFixtureTestCase() {
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    override fun getTestDataPath(): String {
        return "src/test/resources"
    }

    fun testCompletion() {
        myFixture.configureByFiles("CompletionTestData.spec")
        myFixture.complete(CompletionType.BASIC, 1)
        val strings = myFixture.lookupElementStrings
        Assert.assertTrue(strings!!.containsAll(Arrays.asList("pybasever", "pyname", "pyshortver")))
        Assert.assertEquals(3, strings.size)
    }

    /*fun testAnnotator() {
        myFixture.configureByFiles("AnnotatorTestData.java", "DefaultTestData.simple")
        myFixture.checkHighlighting(false, false, true, true)
    }

    un testFormatter() {
        myFixture.configureByFiles("FormatterTestData.simple")
        CodeStyle.getLanguageSettings(myFixture.file).SPACE_AROUND_ASSIGNMENT_OPERATORS = true
        CodeStyle.getLanguageSettings(myFixture.file).KEEP_BLANK_LINES_IN_CODE = 2
        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            CodeStyleManager.getInstance(project).reformatText(myFixture.file,
                    ContainerUtil.newArrayList<TextRange>(myFixture.file.textRange))
        }
        myFixture.checkResultByFile("DefaultTestData.simple")
    }

    fun testRename() {
        myFixture.configureByFiles("RenameTestData.java", "RenameTestData.simple")
        myFixture.renameElementAtCaret("websiteUrl")
        myFixture.checkResultByFile("RenameTestData.simple", "RenameTestDataAfter.simple", false)
    }

    fun testFolding() {
        myFixture.configureByFiles("DefaultTestData.simple")
        myFixture.testFolding("$testDataPath/FoldingTestData.java")
    }

    fun testFindUsages() {
        val usageInfos = myFixture.testFindUsages("ParsingTestData.spec")
        Assert.assertEquals(1, usageInfos.size)
    }

    fun testCommenter() {
        myFixture.configureByText(RpmSpecFileType.INSTANCE, "<caret>website = http://en.wikipedia.org/")
        val commentAction = CommentByLineCommentAction()
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("#website = http://en.wikipedia.org/")
        commentAction.actionPerformedImpl(project, myFixture.editor)
        myFixture.checkResult("website = http://en.wikipedia.org/")
    }*/

    fun testReference() {
        myFixture.configureByFiles("ReferenceTestData.spec")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)!!.parent
        assertEquals("_bi", element.node.findChildByType(RpmSpecTypes.MACRO)!!.text)
    }
}
