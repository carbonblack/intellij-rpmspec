package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.*

class RpmSpecTokenType(@NonNls debugName: String) : IElementType(debugName, RpmSpecLanguage) {
    override fun toString() = "RpmSpecTokenType.${super.toString()}"
}
