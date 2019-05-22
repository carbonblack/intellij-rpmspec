package com.carbonblack.intellij.rpmspec.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

interface RpmSpecMacroElement : PsiNameIdentifierOwner {
    override fun getNameIdentifier(): PsiElement?

    override fun setName(name: String): PsiElement

    override fun getName(): String
}
