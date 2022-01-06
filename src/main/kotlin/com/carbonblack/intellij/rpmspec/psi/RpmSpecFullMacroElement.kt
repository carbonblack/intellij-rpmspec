package com.carbonblack.intellij.rpmspec.psi

import com.intellij.psi.PsiElement

interface RpmSpecFullMacroElement : PsiElement {

    val isConditionalMacro: Boolean

    val isFalseCondition: Boolean
}
