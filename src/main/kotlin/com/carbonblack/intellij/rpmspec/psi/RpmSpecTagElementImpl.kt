package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

abstract class RpmSpecTagElementImpl(node: ASTNode) :
        ASTWrapperPsiElement(node), PsiNameIdentifierOwner {

    override fun getNameIdentifier(): PsiElement? =
        node.findChildByType(RpmSpecTypes.PREAMBLE_TAG)?.psi

    override fun setName(name: String): PsiElement {
        node.findChildByType(RpmSpecTypes.PREAMBLE_TAG)?.let {
            val macro = RpmSpecElementFactory.createTag(project, name)
            val newKeyNode = macro.firstChild.node
            node.replaceChild(it, newKeyNode)
        }
        return this
    }

    override fun getName(): String? {
        return node.findChildByType(RpmSpecTypes.PREAMBLE_TAG)?.text
    }

    override fun getReference(): PsiReference {
        return RpmSpecReference(this, TextRange(0, name?.length ?: 0))
    }
}
