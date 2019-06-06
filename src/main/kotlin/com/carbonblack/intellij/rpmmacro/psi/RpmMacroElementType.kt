package com.carbonblack.intellij.rpmmacro.psi

import com.carbonblack.intellij.rpmmacro.RpmMacroLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.*

class RpmMacroElementType(@NonNls debugName: String) : IElementType(debugName, RpmMacroLanguage)
