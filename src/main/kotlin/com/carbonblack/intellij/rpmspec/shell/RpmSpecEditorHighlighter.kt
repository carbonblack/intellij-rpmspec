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
        val rpmSpecLayerDescriptor = LayerDescriptor(object : SyntaxHighlighter {
            override fun getHighlightingLexer(): Lexer = rpmSpecHighlighter.highlightingLexer

            override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
                return rpmSpecHighlighter.getTokenHighlights(tokenType)
            }
        }, "")

        registerLayer(RpmSpecShellTypes.SPEC_FILE, rpmSpecLayerDescriptor)
        registerLayer(RpmSpecShellTypes.SPEC_FILE_MACRO_LUA, rpmSpecLayerDescriptor)
        registerLayer(RpmSpecShellTypes.SPEC_FILE_MACRO_SHELL, rpmSpecLayerDescriptor)
        registerLayer(RpmSpecShellTypes.SPEC_FILE_MACRO_IDENTIFIER_LUA, rpmSpecLayerDescriptor)
        registerLayer(RpmSpecShellTypes.SPEC_FILE_MACRO_IDENTIFIER_SHELL, rpmSpecLayerDescriptor)
        registerLayer(RpmSpecShellTypes.SPEC_WHITE_SPACE, rpmSpecLayerDescriptor)

        Language.findLanguageByID("Shell Script")?.let { shLanguage ->
            val shellHighlighter =
                SyntaxHighlighterFactory.getSyntaxHighlighter(shLanguage, project, virtualFile)
            val shellLayerDescriptor = LayerDescriptor(object : SyntaxHighlighter {
                override fun getHighlightingLexer(): Lexer = shellHighlighter.highlightingLexer

                override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
                    shellHighlighter.getTokenHighlights(tokenType)
            }, "")

            registerLayer(RpmSpecShellTypes.SHELL_TEXT, shellLayerDescriptor)
            registerLayer(RpmSpecShellTypes.SHELL_WHITE_SPACE, shellLayerDescriptor)
        }
    }
}