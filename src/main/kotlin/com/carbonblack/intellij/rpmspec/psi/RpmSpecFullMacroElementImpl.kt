package com.carbonblack.intellij.rpmspec.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class RpmSpecFullMacroElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    RpmSpecFullMacro {

    override val isConditionalMacro: Boolean = macroConditional != null

    override val isFalseCondition: Boolean
        get() {
            return macroConditional?.text?.contains("!")?.let { negativeConditional ->
                val unresolved = macro?.reference?.resolve() == null

                return unresolved xor negativeConditional
            } ?: false
        }
}
