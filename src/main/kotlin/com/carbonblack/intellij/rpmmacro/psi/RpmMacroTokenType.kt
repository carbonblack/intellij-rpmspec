package com.carbonblack.intellij.rpmmacro.psi

import com.carbonblack.intellij.rpmmacro.RpmMacroLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.*

class RpmMacroTokenType(@NonNls debugName: String) : IElementType(debugName, RpmMacroLanguage) {
    override fun toString(): String = "RpmMacroTokenType." + super.toString()
}
