package com.carbonblack.intellij.rpmmacro

import com.intellij.lexer.FlexAdapter

import java.io.Reader

class RpmMacroLexerAdapter : FlexAdapter(RpmMacroLexer(null))
