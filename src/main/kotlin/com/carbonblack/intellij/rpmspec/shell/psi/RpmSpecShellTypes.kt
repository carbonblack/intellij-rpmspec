package com.carbonblack.intellij.rpmspec.shell.psi

import com.intellij.psi.tree.IElementType

interface RpmSpecShellTypes {
    companion object {
        @JvmField
        val SHELL_TEXT: IElementType = RpmSpecShellElementType("SHELL_TEXT")
        @JvmField
        val SPEC_FILE: IElementType = RpmSpecShellElementType("SPEC_FILE")
    }
}