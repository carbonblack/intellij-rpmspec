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
            RpmSpecTypes.MACRO -> MACRO_KEYS
            RpmSpecTypes.COMMENT -> COMMENT_KEYS
            RpmSpecTypes.IF -> BODY_ITEM_KEYS
            RpmSpecTypes.ELSE -> BODY_ITEM_KEYS
            RpmSpecTypes.ENDIF -> BODY_ITEM_KEYS

            RpmSpecTypes.PREP -> BODY_ITEM_KEYS
            RpmSpecTypes.BUILD -> BODY_ITEM_KEYS
            RpmSpecTypes.INSTALL -> BODY_ITEM_KEYS
            RpmSpecTypes.CHECK -> BODY_ITEM_KEYS
            RpmSpecTypes.FILES -> BODY_ITEM_KEYS
            RpmSpecTypes.POST -> BODY_ITEM_KEYS
            RpmSpecTypes.POSTUN -> BODY_ITEM_KEYS
            RpmSpecTypes.POSTTRANS -> BODY_ITEM_KEYS
            RpmSpecTypes.PRE -> BODY_ITEM_KEYS
            RpmSpecTypes.PREUN -> BODY_ITEM_KEYS
            RpmSpecTypes.PRETRANS -> BODY_ITEM_KEYS
            RpmSpecTypes.CLEAN -> BODY_ITEM_KEYS
            RpmSpecTypes.DESCRIPTION -> BODY_ITEM_KEYS
            RpmSpecTypes.CHANGELOG -> BODY_ITEM_KEYS
            RpmSpecTypes.PACKAGE -> BODY_ITEM_KEYS
            RpmSpecTypes.GLOBAL -> BODY_ITEM_KEYS
            RpmSpecTypes.DEFINE -> BODY_ITEM_KEYS
            RpmSpecTypes.UNDEFINE -> BODY_ITEM_KEYS

            RpmSpecTypes.PREAMBLE_TAG -> KEY_KEYS

            /*RpmSpecTypes.SEPARATOR -> SEPARATOR_KEYS
            RpmSpecTypes.KEY -> KEY_KEYS
            RpmSpecTypes.VALUE -> VALUE_KEYS
            RpmSpecTypes.KEYWORD -> KEY_KEYS
            RpmSpecTypes.BODY_ITEM -> BODY_ITEM_KEYS
            RpmSpecTypes.MACRO_START -> MACRO_KEYS
            RpmSpecTypes.MACRO_END -> MACRO_KEYS
            RpmSpecTypes.MACRO -> MACRO_KEYS
            RpmSpecTypes.MACRO_VALUE -> MACRO_VALUE_KEYS
            RpmSpecTypes.TEXT -> TEXT_KEYS
            RpmSpecTypes.CHANGELOG_DATE -> CHANGELOG_DATE_KEYS
            RpmSpecTypes.CHANGELOG_NAME -> CHANGELOG_NAME_KEYS
            RpmSpecTypes.CHANGELOG_EMAIL -> CHANGELOG_EMAIL_KEYS
            RpmSpecTypes.CHANGELOG_VERSION -> VERSION_KEYS*/
            TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
            else -> EMPTY_KEYS
        }
    }

    companion object {
        val SEPARATOR = createTextAttributesKey("RPM_SPEC_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val KEY = createTextAttributesKey("RPM_SPEC_KEY", DefaultLanguageHighlighterColors.KEYWORD)
        val VALUE = createTextAttributesKey("RPM_SPEC_VALUE", DefaultLanguageHighlighterColors.STRING)
        val BODY_ITEM = createTextAttributesKey("RPM_SPEC_BODY_ITEM", DefaultLanguageHighlighterColors.INSTANCE_METHOD)
        val MACRO_ITEM = createTextAttributesKey("RPM_SPEC_MACRO_ITEM", DefaultLanguageHighlighterColors.CONSTANT)
        val MACRO_VALUE_ITEM = createTextAttributesKey("RPM_SPEC_MACRO_VALUE_ITEM", DefaultLanguageHighlighterColors.CONSTANT)
        val COMMENT = createTextAttributesKey("RPM_SPEC_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val TEXT = createTextAttributesKey("RPM_SPEC_TEXT", DefaultLanguageHighlighterColors.DOC_COMMENT)
        val CHANGELOG_DATE = createTextAttributesKey("RPM_SPEC_CHANGELOG_DATE", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP)
        val CHANGELOG_NAME = createTextAttributesKey("RPM_SPEC_CHANGELOG_NAME", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)
        val CHANGELOG_EMAIL = createTextAttributesKey("RPM_SPEC_CHANGELOG_EMAIL", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG)
        val VERSION = createTextAttributesKey("RPM_VERSION", DefaultLanguageHighlighterColors.NUMBER)
        val BAD_CHARACTER = createTextAttributesKey("RPM_SPEC_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
        private val SEPARATOR_KEYS = arrayOf(SEPARATOR)
        private val KEY_KEYS = arrayOf(KEY)
        private val VALUE_KEYS = arrayOf(VALUE)
        private val BODY_ITEM_KEYS = arrayOf(BODY_ITEM)
        private val MACRO_KEYS = arrayOf(MACRO_ITEM)
        private val MACRO_VALUE_KEYS = arrayOf(MACRO_VALUE_ITEM)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val TEXT_KEYS = arrayOf(TEXT)
        private val CHANGELOG_DATE_KEYS = arrayOf(CHANGELOG_DATE)
        private val CHANGELOG_NAME_KEYS = arrayOf(CHANGELOG_NAME)
        private val CHANGELOG_EMAIL_KEYS = arrayOf(CHANGELOG_EMAIL)
        private val VERSION_KEYS = arrayOf(VERSION)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }
}
