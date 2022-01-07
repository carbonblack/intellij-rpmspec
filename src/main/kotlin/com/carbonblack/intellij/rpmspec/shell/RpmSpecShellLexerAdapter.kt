package com.carbonblack.intellij.rpmspec.shell

import com.intellij.lexer.FlexAdapter

class RpmSpecShellLexerAdapter : FlexAdapter(RpmSpecShellLexer(null))