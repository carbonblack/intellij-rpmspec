package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecFileType
import com.carbonblack.intellij.rpmspec.RpmSpecLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class RpmSpecFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RpmSpecLanguage) {
    override fun getFileType() = RpmSpecFileType

    override fun toString() = "RPM SPEC File"
}
