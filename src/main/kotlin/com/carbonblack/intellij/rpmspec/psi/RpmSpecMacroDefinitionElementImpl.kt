package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

abstract class RpmSpecMacroDefinitionElementImpl(node: ASTNode) :
        ASTWrapperPsiElement(node), PsiNameIdentifierOwner {

    override fun getNameIdentifier(): PsiElement? =
            node.findChildByType(RpmSpecTypes.IDENTIFIER)?.psi

    override fun setName(name: String): PsiElement {
        node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let {
            val macro = RpmSpecElementFactory.createMacro(project, name)
            val newKeyNode = macro.firstChild.node
            node.replaceChild(it, newKeyNode)
        }
        return this
    }

    override fun getName(): String? =
            node.findChildByType(RpmSpecTypes.IDENTIFIER)?.text

    override fun getReference(): PsiReference? {
        node.findChildByType(RpmSpecTypes.IDENTIFIER)?.psi?.let {
            return RpmSpecReference(it, TextRange(0, it.textLength))
        }
        return null
    }

    override fun getTextOffset(): Int {
        return nameIdentifier?.textOffset ?: 0
    }
}
