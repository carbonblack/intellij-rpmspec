package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

abstract class RpmSpecMacroElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), RpmSpecMacroElement {
    override fun getNameIdentifier(): PsiElement? =
        node.findChildByType(RpmSpecTypes.IDENTIFIER)?.psi


    override fun setName(name: String): PsiElement {
        val keyNode = node.findChildByType(RpmSpecTypes.IDENTIFIER)

        if (keyNode != null) {
            val macro = RpmSpecElementFactory.createMacro(project, name)
            val newKeyNode = macro.firstChild.node
            node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }

    override fun getName(): String {
        val valueNode = node.findChildByType(RpmSpecTypes.IDENTIFIER)
        return valueNode!!.text
    }

    override fun getReference(): PsiReference {
        return RpmSpecReference(this, TextRange(0, name.length))
    }
}
