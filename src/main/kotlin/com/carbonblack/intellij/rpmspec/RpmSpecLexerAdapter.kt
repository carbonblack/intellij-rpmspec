package com.carbonblack.intellij.rpmspec

import com.intellij.lexer.FlexAdapter

class RpmSpecLexerAdapter : FlexAdapter(RpmSpecLexer(null))
