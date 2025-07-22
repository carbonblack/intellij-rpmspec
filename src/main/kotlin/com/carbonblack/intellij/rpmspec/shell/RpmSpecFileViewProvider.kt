package com.carbonblack.intellij.rpmspec.shell

import com.carbonblack.intellij.rpmspec.RpmSpecLanguage
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecOuterElementType
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecShellTypes
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecTemplateDataElementType
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.intellij.psi.tree.TokenSet

private val OUTER_SPEC = RpmSpecOuterElementType("RPM Spec Element")

private val TEMPLATE_DATA_ELEMENT = RpmSpecTemplateDataElementType(
    "RPM Spec Shell Template",
    RpmSpecLanguage,
    TokenSet.create(
        RpmSpecShellTypes.SHELL_TEXT,
        RpmSpecShellTypes.SPEC_FILE_MACRO_IDENTIFIER_SHELL,
        RpmSpecShellTypes.SHELL_WHITE_SPACE,
        RpmSpecShellTypes.SPEC_WHITE_SPACE,
        RpmSpecShellTypes.LUA_WHITE_SPACE,
    ),
    OUTER_SPEC,
)

class RpmSpecFileViewProvider(
    manager: PsiManager,
    virtualFile: VirtualFile,
    eventSystemEnabled: Boolean,
) : MultiplePsiFilesPerDocumentFileViewProvider(manager, virtualFile, eventSystemEnabled),
    TemplateLanguageFileViewProvider {

    override fun getBaseLanguage(): Language = RpmSpecLanguage
    override fun getTemplateDataLanguage(): Language = Language.findLanguageByID("Shell Script") ?: PlainTextLanguage.INSTANCE
    override fun getLanguages(): MutableSet<Language> = mutableSetOf(RpmSpecLanguage, templateDataLanguage)

    override fun cloneInner(fileCopy: VirtualFile): MultiplePsiFilesPerDocumentFileViewProvider =
        RpmSpecFileViewProvider(manager, fileCopy, false)

    override fun createFile(lang: Language): PsiFile? {
        val parser = LanguageParserDefinitions.INSTANCE.forLanguage(lang)
        return when {
            parser == null -> null
            lang === this.baseLanguage -> parser.createFile(this)
            lang === templateDataLanguage -> (parser.createFile(this) as PsiFileImpl).apply {
                contentElementType = TEMPLATE_DATA_ELEMENT
            }
            else -> null
        }
    }
}
