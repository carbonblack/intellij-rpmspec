package com.carbonblack.intellij.rpmmacro.psi

import com.carbonblack.intellij.rpmmacro.RpmMacroFileType
import com.carbonblack.intellij.rpmmacro.RpmMacroLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class RpmMacroFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RpmMacroLanguage) {
    override fun getFileType(): FileType = RpmMacroFileType

    override fun toString(): String = "RPM Macro File"
}
