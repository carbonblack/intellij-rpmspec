package com.carbonblack.intellij.rpmmacro.psi

import com.carbonblack.intellij.rpmmacro.RpmMacroReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

abstract class RpmMacroMacroElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    RpmMacroMacroElement {
    override fun getNameIdentifier() = node.findChildByType(RpmMacroTypes.IDENTIFIER)?.psi

    override fun setName(name: String): PsiElement {
        node.findChildByType(RpmMacroTypes.IDENTIFIER)?.let { keyNode ->
            val macro = RpmMacroElementFactory.createMacro(project, name)
            val newKeyNode = macro.firstChild.node
            node.replaceChild(keyNode, newKeyNode)
        }
        return this
    }

    override fun getName(): String {
        val valueNode = node.findChildByType(RpmMacroTypes.IDENTIFIER)
        return valueNode?.text ?: ""
    }

    override fun getReference(): PsiReference = RpmMacroReference(this, TextRange(0, name.length))
}
