package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

class RpmMacroSyntaxHighligher : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = RpmMacroLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            RpmMacroTypes.MACRO -> MACRO_KEYS
            RpmMacroTypes.COMMENT -> COMMENT_KEYS
            RpmMacroTypes.MACRO_COMMENT -> COMMENT_KEYS
            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
            else -> EMPTY_KEYS
        }
    }

    companion object {
        val MACRO_ITEM = createTextAttributesKey("RPM_MACRO_MACRO_ITEM", DefaultLanguageHighlighterColors.CONSTANT)
        val COMMENT = createTextAttributesKey("RPM_MACRO_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER = createTextAttributesKey("RPM_SPEC_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
        private val MACRO_KEYS = arrayOf(MACRO_ITEM)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }
}
