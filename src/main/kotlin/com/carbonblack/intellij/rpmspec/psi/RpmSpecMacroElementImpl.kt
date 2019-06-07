package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
import com.intellij.psi.tree.IElementType

abstract class RpmSpecMacroElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), RpmSpecMacroElement, ContributedReferenceHost  { //, ContributedReferenceHost {
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

    override fun getReference(): PsiReference
        = RpmSpecReference(this, TextRange(0, name.length))

    override fun getReferences(): Array<PsiReference>
        = ReferenceProvidersRegistry.getReferencesFromProviders(this)
}
