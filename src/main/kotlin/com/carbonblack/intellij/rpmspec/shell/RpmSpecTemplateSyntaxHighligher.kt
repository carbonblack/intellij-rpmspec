package com.carbonblack.intellij.rpmspec.shell

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class RpmSpecTemplateSyntaxHighligher : SyntaxHighlighterBase() {

    override fun getHighlightingLexer() = RpmSpecShellLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType) = EMPTY_KEYS

    companion object {
        private val EMPTY_KEYS = arrayOf<TextAttributesKey>()
    }
}