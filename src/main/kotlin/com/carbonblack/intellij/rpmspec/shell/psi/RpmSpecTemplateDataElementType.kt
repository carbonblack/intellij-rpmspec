package com.carbonblack.intellij.rpmspec.shell.psi

import com.carbonblack.intellij.rpmspec.shell.RpmSpecShellLexerAdapter
import com.intellij.lang.Language
import com.intellij.lexer.Lexer
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.intellij.psi.tree.IElementType

class RpmSpecTemplateDataElementType(
    debugName: String,
    language: Language?,
    templateElementType: IElementType,
    outerElementType: IElementType
) :
    TemplateDataElementType(debugName, language, templateElementType, outerElementType) {

    override fun createBaseLexer(viewProvider: TemplateLanguageFileViewProvider?): Lexer = RpmSpecShellLexerAdapter()
}