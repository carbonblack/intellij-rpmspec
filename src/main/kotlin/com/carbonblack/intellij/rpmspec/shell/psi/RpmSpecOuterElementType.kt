package com.carbonblack.intellij.rpmspec.shell.psi

import com.carbonblack.intellij.rpmspec.RpmSpecLanguage
import com.intellij.lang.ASTNode
import com.intellij.psi.templateLanguages.OuterLanguageElementImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.ILeafElementType

class RpmSpecOuterElementType(debugName: String) :
    IElementType(debugName, RpmSpecLanguage),
    ILeafElementType {

    override fun createLeafNode(charSequence: CharSequence): ASTNode = OuterLanguageElementImpl(this, charSequence)
}
