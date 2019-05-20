package com.carbonblack.intellij.rpmspec;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes;
import com.intellij.psi.TokenType;

%%

%class RpmSpecLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
FIRST_VALUE_CHARACTER=[^ \n\f\\] | "\\"{CRLF} | "\\".
VALUE_CHARACTER=[^\n\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT=("#"|"!")[^\r\n]*
SEPARATOR=[:]
KEY_CHARACTER=[^:=\ \n\t\f\\] | "\\ "
BODY_ITEM=%(description|prep|build|install|check|files|changelog)

%state WAITING_VALUE

%%

<YYINITIAL> {BODY_ITEM}{VALUE_CHARACTER}*                   { yybegin(YYINITIAL); return RpmSpecTypes.BODY_ITEM; }

<YYINITIAL> {END_OF_LINE_COMMENT}                           { yybegin(YYINITIAL); return RpmSpecTypes.COMMENT; }

<YYINITIAL> {KEY_CHARACTER}+                                { yybegin(YYINITIAL); return RpmSpecTypes.KEY; }

<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return RpmSpecTypes.SEPARATOR; }

<WAITING_VALUE> {CRLF}({CRLF}|{WHITE_SPACE})+               { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {FIRST_VALUE_CHARACTER}{VALUE_CHARACTER}*   { yybegin(YYINITIAL); return RpmSpecTypes.VALUE; }

({CRLF}|{WHITE_SPACE})+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }
