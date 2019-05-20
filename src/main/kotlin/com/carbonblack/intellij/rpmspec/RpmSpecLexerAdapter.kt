package com.carbonblack.intellij.rpmspec

import com.intellij.lexer.FlexAdapter

import java.io.Reader

class RpmSpecLexerAdapter : FlexAdapter(RpmSpecLexer(null as Reader?))
