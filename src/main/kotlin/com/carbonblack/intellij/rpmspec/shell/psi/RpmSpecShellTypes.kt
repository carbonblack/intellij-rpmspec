package com.carbonblack.intellij.rpmspec.shell.psi

import com.intellij.psi.tree.IElementType

interface RpmSpecShellTypes {
    companion object {
        @JvmField
        val SHELL_TEXT: IElementType = RpmSpecShellElementType("SHELL_TEXT")

        @JvmField
        val LUA_TEXT: IElementType = RpmSpecShellElementType("LUA_TEXT")

        @JvmField
        val SPEC_FILE: IElementType = RpmSpecShellElementType("SPEC_FILE")

        @JvmField
        val SPEC_FILE_MACRO_SHELL: IElementType = RpmSpecShellElementType("SPEC_FILE_MACRO_SHELL")

        @JvmField
        val SPEC_FILE_MACRO_LUA: IElementType = RpmSpecShellElementType("SPEC_FILE_MACRO_LUA")

        @JvmField
        val SPEC_FILE_MACRO_IDENTIFIER_SHELL: IElementType = RpmSpecShellElementType("SPEC_FILE_MACRO_IDENTIFIER_SHELL")

        @JvmField
        val SPEC_FILE_MACRO_IDENTIFIER_LUA: IElementType = RpmSpecShellElementType("SPEC_FILE_MACRO_IDENTIFIER_LUA")

        @JvmField
        val SHELL_WHITE_SPACE: IElementType = RpmSpecShellElementType("SHELL_WHITE_SPACE")

        @JvmField
        val LUA_WHITE_SPACE: IElementType = RpmSpecShellElementType("LUA_WHITE_SPACE")

        @JvmField
        val SPEC_WHITE_SPACE: IElementType = RpmSpecShellElementType("SPEC_WHITE_SPACE")
    }
}
