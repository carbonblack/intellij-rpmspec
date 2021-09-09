package com.carbonblack.intellij.rpmspec.shell

import com.carbonblack.intellij.rpmspec.RpmSpecSyntaxHighligher
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecShellTypes
import com.intellij.lang.Language
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.util.LayerDescriptor
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

class RpmSpecEditorHighlighter(project: Project?, virtualFile: VirtualFile?, colors: EditorColorsScheme) :
    LayeredLexerEditorHighlighter(RpmSpecTemplateSyntaxHighligher(), colors) {

    init {
        val rpmSpecHighlighter = RpmSpecSyntaxHighligher()
        registerLayer(RpmSpecShellTypes.SPEC_FILE, LayerDescriptor(object : SyntaxHighlighter {
            override fun getHighlightingLexer(): Lexer = rpmSpecHighlighter.highlightingLexer

            override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
                return rpmSpecHighlighter.getTokenHighlights(tokenType)
            }
        }, "\n"))

        Language.findLanguageByID("Shell Script")?.let { shLanguage ->
            val shellHighlighter =
                SyntaxHighlighterFactory.getSyntaxHighlighter(shLanguage, project, virtualFile)
            registerLayer(RpmSpecShellTypes.SHELL_TEXT, LayerDescriptor(object : SyntaxHighlighter {
                override fun getHighlightingLexer(): Lexer = shellHighlighter.highlightingLexer

                override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
                    shellHighlighter.getTokenHighlights(tokenType)
            }, ""))
        }
    }
}