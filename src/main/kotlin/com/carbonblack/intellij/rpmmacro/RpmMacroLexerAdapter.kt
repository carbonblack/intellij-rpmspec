package com.carbonblack.intellij.rpmmacro

import com.intellij.lexer.FlexAdapter

class RpmMacroLexerAdapter : FlexAdapter(RpmMacroLexer(null))
