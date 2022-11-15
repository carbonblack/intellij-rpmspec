package com.carbonblack.intellij.rpmspec.psi

import com.intellij.psi.PsiNameIdentifierOwner

// Taken from https://github.com/rpm-software-management/rpm/blob/rpm-4.17.0-release/rpmio/macro.c#L1258-L1286
private val BUILT_IN_MACROS = listOf(
    "P", "S", "basename", "define", "dirname", "dnl",
    "dump", "echo", "error", "exists", "expand", "expr",
    "getconfdir", "getenv", "getncpus", "global", "load", "lua", "macrobody",
    "quote", "shrink", "suffix", "trace", "u2p", "shescape", "uncompress",
    "undefine", "url2path", "verbose", "warn",
)

interface RpmSpecMacroElement : PsiNameIdentifierOwner {

    val isBuiltInMacro: Boolean

    companion object {
        val builtInMacros = BUILT_IN_MACROS
    }
}
