package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

abstract class RpmSpecMacroElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PsiNameIdentifierOwner, RpmSpecMacroElement {

    override fun getNameIdentifier() = node.findChildByType(RpmSpecTypes.IDENTIFIER)?.psi

    override fun setName(name: String): PsiElement {
        node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let {
            val macro = RpmSpecElementFactory.createMacro(project, name)
            val newKeyNode = macro.firstChild.node
            node.replaceChild(it, newKeyNode)
        }
        return this
    }

    override fun getName() = node.findChildByType(RpmSpecTypes.IDENTIFIER)?.text

    override fun getReference() = RpmSpecReference(this, TextRange(0, name?.length ?: 0))

    override val isBuiltInMacro = (name?.toLowerCase() ?: "") in RpmSpecMacroElement.builtInMacros
}
