package com.carbonblack.intellij.rpmmacro;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.carbonblack.intellij.rpmmacro.psi.RpmMacroTypes.*;

%%

%class RpmMacroLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}


// Whitespace
EOL              = \n | \r | \r\n
LINE_WS          = [\ \t\f]
WHITE_SPACE_CHAR = {EOL} | {LINE_WS}
WHITE_SPACE      = {WHITE_SPACE_CHAR}+

// Literals
INPUT_CHARACTER =  [^\r\n]

ALPHA           = [a-zA-Z]
NUMERIC         = [0-9]
IDENTIFIER_CHAR = {ALPHA}|{NUMERIC}|"_"

IDENTIFIER      = {IDENTIFIER_CHAR}+
CODE_CHARS      = [^\r\n\ \t\f{}()%:?\\]+

INT_LITERAL   = {NUMERIC}+
FLT_LITERAL   = {INT_LITERAL} \. {INT_LITERAL}

COMMENT       = "#" {INPUT_CHARACTER}*
MACRO_ESCAPE  = "\\"{WHITE_SPACE}*{EOL}

%state MACRO

%%


<YYINITIAL> {
  ^"%"                            { yybegin(MACRO); return MACRO_DEFINITION_START; }
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

  /* LITERALS */
  {FLT_LITERAL}                   { return FLOAT_LITERAL; }
  {INT_LITERAL}                   { return INTEGER_LITERAL; }

  {IDENTIFIER}                    { return IDENTIFIER; }

  {CODE_CHARS}                    { return CODE; }
  {MACRO_ESCAPE}                  { return CODE; }
}

<MACRO> {
  ^{COMMENT}{MACRO_ESCAPE}        { return MACRO_COMMENT; }
  {EOL}+                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
  {LINE_WS}+                      { return TokenType.WHITE_SPACE; }
}

[^]                               { return TokenType.BAD_CHARACTER; }
