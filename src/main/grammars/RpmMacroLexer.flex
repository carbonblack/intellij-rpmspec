package com.carbonblack.intellij.rpmmacro;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import java.util.Stack;

import static com.carbonblack.intellij.rpmmacro.psi.RpmMacroTypes.*;

%%

%class RpmMacroLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}


///////////////////////////////////////////////////////////////////////////////////////////////////
// Whitespaces
///////////////////////////////////////////////////////////////////////////////////////////////////

EOL              = \n | \r | \r\n
LINE_WS          = [\ \t\f]
WHITE_SPACE_CHAR = {EOL} | {LINE_WS}
WHITE_SPACE      = {WHITE_SPACE_CHAR}+


INPUT_CHARACTER =  [^\r\n]

///////////////////////////////////////////////////////////////////////////////////////////////////
// Identifier
///////////////////////////////////////////////////////////////////////////////////////////////////

ALPHA           = [a-zA-Z]
NUMERIC         = [0-9]
IDENTIFIER_CHAR = {ALPHA}|{NUMERIC}|"_"

IDENTIFIER      = {IDENTIFIER_CHAR}+
SUFFIX          = {IDENTIFIER}
CODE            = [^\r\n\ \t\f{}()%:?\\]+


EXPONENT      = [eE] [-+]? [0-9_]+


FLT_LITERAL   = ( {DEC_LITERAL} \. {DEC_LITERAL} {EXPONENT}? {SUFFIX}? )
              | ( {DEC_LITERAL} {EXPONENT} {SUFFIX}? )
              | ( {DEC_LITERAL} "f" [\p{xidcontinue}]* )

FLT_LITERAL_TDOT = {DEC_LITERAL} \.

INT_LITERAL = ( {DEC_LITERAL}
              | {HEX_LITERAL}
              | {OCT_LITERAL}
              | {BIN_LITERAL} ) {SUFFIX}?

DEC_LITERAL = [0-9] [0-9_]*
HEX_LITERAL = "0x" [a-fA-F0-9_]*
OCT_LITERAL = "0o" [0-7_]*
BIN_LITERAL = "0b" [01_]*

COMMENT   =   "#" {INPUT_CHARACTER}*

%state MACRO

%%


<YYINITIAL> {
  "%"                             { yybegin(MACRO); return MACRO_DEFINITION_START; }
  {WHITE_SPACE}                   { return TokenType.WHITE_SPACE; }
}

<YYINITIAL, MACRO> {
  "%"                             { return PERCENT; }
  "{"                             { return LBRACE; }
  "}"                             { return RBRACE; }
  "("                             { return LPAREN; }
  ")"                             { return RPAREN; }
  ":"                             { return COLON; }
  "?"                             { return QUESTION_MARK; }
  "\\"                            { return BACKSLASH; }
  "\\\\"                          { return ESCAPED_BACKSLASH; }

  ^{COMMENT}                      { return COMMENT; }
  {IDENTIFIER}                    { return IDENTIFIER; }

  /* LITERALS */

  // Floats must come first, to parse 1e1 as a float and not as an integer with a suffix
  {FLT_LITERAL}                   { return FLOAT_LITERAL; }
  {INT_LITERAL}                   { return INTEGER_LITERAL; }

  {CODE}                          { return CODE; }
  "\\"{WHITE_SPACE}*{EOL}         { return CODE; }
}

<MACRO> {
  {EOL}+                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
  {LINE_WS}+                      { return TokenType.WHITE_SPACE; }
}

[^]                               { return TokenType.BAD_CHARACTER; }
