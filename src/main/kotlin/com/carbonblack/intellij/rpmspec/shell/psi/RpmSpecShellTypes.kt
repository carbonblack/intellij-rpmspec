package com.carbonblack.intellij.rpmspec.shell.psi

import com.intellij.psi.tree.IElementType

interface RpmSpecShellTypes {
    companion object {
        @JvmField
        val SHELL_TEXT: IElementType = RpmSpecShellElementType("SHELL_TEXT")
        @JvmField
        val SPEC_FILE: IElementType = RpmSpecShellElementType("SPEC_FILE")
        @JvmField
        val SPEC_FILE_MACRO: IElementType = RpmSpecShellElementType("SPEC_FILE_MACRO")
        @JvmField
        val SPEC_FILE_MACRO_IDENTIFIER: IElementType = RpmSpecShellElementType("SPEC_FILE_MACRO_IDENTIFIER")
        @JvmField
        val SHELL_WHITE_SPACE: IElementType = RpmSpecShellElementType("SHELL_WHITE_SPACE")
        @JvmField
        val SPEC_WHITE_SPACE: IElementType = RpmSpecShellElementType("SPEC_WHITE_SPACE")
    }
}