package com.carbonblack.intellij.rpmspec.psi

import com.intellij.psi.PsiElement

interface RpmSpecPropertyElement : PsiElement {
    @JvmDefault
    val key: String?
        get() {
            val keyNode = node.findChildByType(RpmSpecTypes.KEY)
            return keyNode?.text?.replace("\\\\ ".toRegex(), " ")
        }

    @JvmDefault
    val value: String?
        get() {
            val valueNode = node.findChildByType(RpmSpecTypes.VALUE)
            return valueNode?.text
        }
}
