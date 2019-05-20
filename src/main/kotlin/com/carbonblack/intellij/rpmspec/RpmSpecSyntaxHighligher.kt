package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

class RpmSpecSyntaxHighligher : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = RpmSpecLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            RpmSpecTypes.SEPARATOR -> SEPARATOR_KEYS
            RpmSpecTypes.KEY -> KEY_KEYS
            RpmSpecTypes.VALUE -> VALUE_KEYS
            RpmSpecTypes.BODY_ITEM -> BODY_ITEM_KEYS
            RpmSpecTypes.COMMENT -> COMMENT_KEYS
            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
            else -> EMPTY_KEYS
        }
    }

    companion object {
        val SEPARATOR = createTextAttributesKey("RPM_SPEC_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val KEY = createTextAttributesKey("RPM_SPEC_KEY", DefaultLanguageHighlighterColors.KEYWORD)
        val VALUE = createTextAttributesKey("RPM_SPEC_VALUE", DefaultLanguageHighlighterColors.STRING)
        val BODY_ITEM = createTextAttributesKey("RPM_SPEC_BODY_ITEM", DefaultLanguageHighlighterColors.CLASS_NAME)
        val COMMENT = createTextAttributesKey("RPM_SPEC_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER = createTextAttributesKey("RPM_SPEC_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
        private val SEPARATOR_KEYS = arrayOf(SEPARATOR)
        private val KEY_KEYS = arrayOf(KEY)
        private val VALUE_KEYS = arrayOf(VALUE)
        private val BODY_ITEM_KEYS = arrayOf(BODY_ITEM)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }
}
