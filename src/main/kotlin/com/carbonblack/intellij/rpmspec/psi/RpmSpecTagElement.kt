package com.carbonblack.intellij.rpmspec.psi

import com.intellij.psi.PsiNameIdentifierOwner

interface RpmSpecTagElement : PsiNameIdentifierOwner {

    fun knownTag() : Boolean
}
