package com.carbonblack.intellij.rpmspec.shell

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager

class RpmSpecFileViewProviderFactory : FileViewProviderFactory {
    override fun createFileViewProvider(
        virtualFile: VirtualFile,
        language: Language,
        psiManager: PsiManager,
        physical: Boolean
    ) = RpmSpecFileViewProvider(psiManager, virtualFile, physical)
}