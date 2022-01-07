package com.carbonblack.intellij.rpmspec.shell.psi

import com.carbonblack.intellij.rpmspec.shell.RpmSpecShellLexerAdapter
import com.intellij.lang.Language
import com.intellij.lexer.Lexer
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

class RpmSpecTemplateDataElementType(
    debugName: String,
    language: Language?,
    private val templateElementTypes: TokenSet,
    outerElementType: IElementType
) :
    TemplateDataElementType(debugName, language, templateElementTypes.types.first(), outerElementType) {

    override fun createTemplateText(
        sourceCode: CharSequence,
        baseLexer: Lexer,
        rangeCollector: RangeCollector
    ): CharSequence {
        val result = StringBuilder(sourceCode.length)
        baseLexer.start(sourceCode)
        var currentRange = TextRange.EMPTY_RANGE

        while (baseLexer.tokenType != null) {
            val newRange = TextRange.create(baseLexer.tokenStart, baseLexer.tokenEnd)
            assert(currentRange.endOffset == newRange.startOffset) {
                "Inconsistent tokens stream from " + baseLexer +
                        ": " + getRangeDump(
                    currentRange,
                    sourceCode
                ) + " followed by " + getRangeDump(newRange, sourceCode)
            }

            currentRange = newRange
            if (templateElementTypes.contains(baseLexer.tokenType)) {
                result.append(sourceCode, baseLexer.tokenStart, baseLexer.tokenEnd)
            } else if (baseLexer.tokenType == RpmSpecShellTypes.SPEC_FILE_MACRO_SHELL && baseLexer.tokenText.length > 1) {
                rangeCollector.addOuterRange(TextRange.create(baseLexer.tokenStart, baseLexer.tokenStart + 1))
                result.append(sourceCode, baseLexer.tokenStart + 1, baseLexer.tokenEnd)
            } else {
                rangeCollector.addOuterRange(currentRange)
            }
            baseLexer.advance()
        }
        return result
    }

    private fun getRangeDump(range: TextRange, sequence: CharSequence): String {
        return "'" + StringUtil.escapeLineBreak(range.subSequence(sequence).toString()) + "' " + range
    }

    override fun createBaseLexer(viewProvider: TemplateLanguageFileViewProvider?): Lexer = RpmSpecShellLexerAdapter()
}