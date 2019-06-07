package com.carbonblack.intellij.rpmmacro.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

interface RpmMacroMacroElement : PsiNameIdentifierOwner {
    override fun getNameIdentifier(): PsiElement?

    override fun setName(name: String): PsiElement

    override fun getName(): String
}
