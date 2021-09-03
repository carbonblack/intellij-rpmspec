package com.carbonblack.intellij.rpmspec.shell

import com.carbonblack.intellij.rpmspec.RpmSpecLanguage
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecOuterElementType
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecShellTypes
import com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecTemplateDataElementType
import com.intellij.lang.Language
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.intellij.sh.ShLanguage

private val OUTER_SPEC = RpmSpecOuterElementType("RPM Spec Element")

class RpmSpecFileViewProvider(
    manager: PsiManager,
    virtualFile: VirtualFile,
    eventSystemEnabled: Boolean
) : MultiplePsiFilesPerDocumentFileViewProvider(manager, virtualFile, eventSystemEnabled),
    TemplateLanguageFileViewProvider {

    private val templateDataElement =
        RpmSpecTemplateDataElementType(
            "RPM Spec Shell Template",
            RpmSpecLanguage,
            RpmSpecShellTypes.SHELL_TEXT,
            OUTER_SPEC
        )

    override fun getBaseLanguage(): Language = RpmSpecLanguage
    override fun getTemplateDataLanguage(): Language = ShLanguage.INSTANCE
    override fun getLanguages(): MutableSet<Language> = mutableSetOf(RpmSpecLanguage, templateDataLanguage)

    override fun cloneInner(fileCopy: VirtualFile): MultiplePsiFilesPerDocumentFileViewProvider =
        RpmSpecFileViewProvider(manager, fileCopy, false)

    override fun createFile(lang: Language): PsiFile? {
        val parser = LanguageParserDefinitions.INSTANCE.forLanguage(lang)
        return when {
            parser == null -> null
            lang === this.baseLanguage -> parser.createFile(this)
            lang === templateDataLanguage -> (parser.createFile(this) as PsiFileImpl).apply {
                contentElementType = templateDataElement
            }
            else -> null
        }
    }
}