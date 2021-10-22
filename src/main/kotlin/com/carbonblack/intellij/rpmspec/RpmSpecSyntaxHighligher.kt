package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

class RpmSpecSyntaxHighligher : SyntaxHighlighterBase() {

    override fun getHighlightingLexer() = RpmSpecLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType) = when (tokenType) {
        RpmSpecTypes.MACRO -> MACRO_KEYS
        RpmSpecTypes.COMMENT -> COMMENT_KEYS
        RpmSpecTypes.IF -> RESERVED_KEYS
        RpmSpecTypes.ELSE -> RESERVED_KEYS
        RpmSpecTypes.ENDIF -> RESERVED_KEYS
        RpmSpecTypes.SETUP -> RESERVED_KEYS

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
        RpmSpecTypes.TRIGGERIN -> BODY_ITEM_KEYS
        RpmSpecTypes.TRIGGERUN -> BODY_ITEM_KEYS
        RpmSpecTypes.TRIGGERPOSTUN -> BODY_ITEM_KEYS
        RpmSpecTypes.FILETRIGGERIN -> BODY_ITEM_KEYS
        RpmSpecTypes.FILETRIGGERUN -> BODY_ITEM_KEYS
        RpmSpecTypes.FILETRIGGERPOSTUN -> BODY_ITEM_KEYS
        RpmSpecTypes.TRANSFILETRIGGERIN -> BODY_ITEM_KEYS
        RpmSpecTypes.TRANSFILETRIGGERUN -> BODY_ITEM_KEYS
        RpmSpecTypes.TRANSFILETRIGGERPOSTUN -> BODY_ITEM_KEYS
        RpmSpecTypes.CLEAN -> BODY_ITEM_KEYS
        RpmSpecTypes.DESCRIPTION -> BODY_ITEM_KEYS
        RpmSpecTypes.CHANGELOG -> BODY_ITEM_KEYS
        RpmSpecTypes.PACKAGE -> BODY_ITEM_KEYS
        RpmSpecTypes.GLOBAL -> BODY_ITEM_KEYS
        RpmSpecTypes.DEFINE -> BODY_ITEM_KEYS
        RpmSpecTypes.UNDEFINE -> BODY_ITEM_KEYS

        RpmSpecTypes.FILES_DIRECTIVE_TOKEN -> KEY_KEYS

        TokenType.BAD_CHARACTER -> BAD_CHAR_KEYS
        else -> EMPTY_KEYS
    }

    companion object {
        val SEPARATOR = createTextAttributesKey("RPM_SPEC_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val KEY = createTextAttributesKey("RPM_SPEC_KEY", DefaultLanguageHighlighterColors.KEYWORD)
        val VALUE = createTextAttributesKey("RPM_SPEC_VALUE", DefaultLanguageHighlighterColors.STRING)
        val BODY_ITEM = createTextAttributesKey("RPM_SPEC_BODY_ITEM", DefaultLanguageHighlighterColors.INSTANCE_METHOD)
        val RESERVED = createTextAttributesKey("RPM_SPEC_RESERVED", DefaultLanguageHighlighterColors.INSTANCE_METHOD)
        val MACRO_ITEM = createTextAttributesKey("RPM_SPEC_MACRO_ITEM", DefaultLanguageHighlighterColors.CONSTANT)
        val MACRO_VALUE_ITEM = createTextAttributesKey("RPM_SPEC_MACRO_VALUE_ITEM", DefaultLanguageHighlighterColors.CONSTANT)
        val COMMENT = createTextAttributesKey("RPM_SPEC_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BRACES = createTextAttributesKey("RPM_SPEC_CODE", DefaultLanguageHighlighterColors.IDENTIFIER)
        val TEXT = createTextAttributesKey("RPM_SPEC_TEXT", DefaultLanguageHighlighterColors.DOC_COMMENT)
        val CHANGELOG_DATE = createTextAttributesKey("RPM_SPEC_CHANGELOG_DATE", DefaultLanguageHighlighterColors.DOC_COMMENT_MARKUP)
        val CHANGELOG_NAME = createTextAttributesKey("RPM_SPEC_CHANGELOG_NAME", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)
        val CHANGELOG_EMAIL = createTextAttributesKey("RPM_SPEC_CHANGELOG_EMAIL", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG)
        val VERSION = createTextAttributesKey("RPM_VERSION", DefaultLanguageHighlighterColors.NUMBER)
        private val BAD_CHARACTER = createTextAttributesKey("RPM_SPEC_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
        private val KEY_KEYS = arrayOf(KEY)
        private val RESERVED_KEYS = arrayOf(RESERVED)
        private val BODY_ITEM_KEYS = arrayOf(BODY_ITEM)
        private val MACRO_KEYS = arrayOf(MACRO_ITEM)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }
}
