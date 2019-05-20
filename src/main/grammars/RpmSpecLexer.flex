package com.carbonblack.intellij.rpmspec;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes;
import com.intellij.psi.TokenType;
import java.util.Stack;

%%

%class RpmSpecLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
    private Stack<Integer> stack = new Stack<>();

    private void yypushState(int newState) {
      stack.push(yystate());
      yybegin(newState);
    }

    private void yypopState() {
      yybegin(stack.pop());
    }
%}

CRLF=[\r\n]
WHITE_SPACE=[\ \t\f]
COMMENT=("#")[^\r\n]*
SEPARATOR=:
PREAMBLE_KEY_CHARACTER=[a-zA-Z0-9]
COMMAND_SECTIONS=%(prep|build|install|check|files)
KEYWORDS=%(if|else|endif|ifarch)

%state PREAMBLE_VALUE
%state MACRO_EXPANSION
%state MACRO_SIMPLE_FORM
%state MACRO_DEFINITION_NAME
%state MACRO_DEFINITION_VALUE
%state MACRO_ARGUMENTS
%state DESCRIPTION
%state COMMAND_SECTION
%state CHANGELOG_HEADER
%state CHANGELOG
%state PACKAGE_NAME

%%

<YYINITIAL> {
      {PREAMBLE_KEY_CHARACTER}+                   { return RpmSpecTypes.KEY; }
      {SEPARATOR}                                 { yybegin(PREAMBLE_VALUE); return RpmSpecTypes.SEPARATOR; }
}

<MACRO_ARGUMENTS> {
      [^\r\n%#]+                                  { return RpmSpecTypes.CODE; }
      {CRLF}+                                     { yypopState(); return TokenType.WHITE_SPACE; }
}

<COMMAND_SECTION> {
      [^\r\n%#][^%#]*                             { return RpmSpecTypes.CODE; }
}

<PREAMBLE_VALUE> {
      [^\r\n%]+                                   { return RpmSpecTypes.VALUE; }
      {CRLF}+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
}

<MACRO_EXPANSION> {
      [^}%]+                                      { return RpmSpecTypes.MACRO_VALUE; }
      \}                                          { yypopState(); return RpmSpecTypes.MACRO_END; }
}

<MACRO_SIMPLE_FORM> {
      \S+                                         { yypopState(); return RpmSpecTypes.MACRO_VALUE; }
}

<MACRO_DEFINITION_NAME> {
      \S+                                         { yybegin(MACRO_DEFINITION_VALUE); return RpmSpecTypes.MACRO_VALUE; }
}

<MACRO_DEFINITION_VALUE> {
      [^\r\n%]+                                   { return RpmSpecTypes.VALUE; }
      {CRLF}+                                     { yypopState(); return TokenType.WHITE_SPACE; }
}

<DESCRIPTION> {
      [^\r\n%][^%]*                               { return RpmSpecTypes.TEXT; }
      ^%\{                                        { yypushState(MACRO_EXPANSION); return RpmSpecTypes.MACRO_START; }
      ^%                                          { yypushState(MACRO_SIMPLE_FORM); return RpmSpecTypes.MACRO_START; }
}

<CHANGELOG_HEADER> {
      \*\s+\w+\s+\w+\s+\d+\s+\d+                  { return RpmSpecTypes.CHANGELOG_DATE; }
      \w+\s+\w+(\s+\w+)?                          { return RpmSpecTypes.CHANGELOG_NAME; }
      \<\S+\>                                     { return RpmSpecTypes.CHANGELOG_EMAIL; }
      \s+-\s+                                     { return RpmSpecTypes.TEXT; }
      \S+                                         { yybegin(CHANGELOG); return RpmSpecTypes.CHANGELOG_VERSION; }
}

<CHANGELOG> {
      [^*][^\r\n]+                                { return RpmSpecTypes.TEXT; }
      [\r\n]*[^*\r\n]+                            { return RpmSpecTypes.TEXT; }
      {CRLF}+                                     { yybegin(CHANGELOG_HEADER); return TokenType.WHITE_SPACE; }
}

<PACKAGE_NAME> {
    [^\r\n%]+                                     { return RpmSpecTypes.VALUE; }
    {CRLF}+                                       { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
}

{KEYWORDS}                                        { yypushState(MACRO_ARGUMENTS); return RpmSpecTypes.KEYWORD; }
^%\{                                              { yypushState(MACRO_ARGUMENTS); yypushState(MACRO_EXPANSION); return RpmSpecTypes.MACRO_START; }
^%                                                { yypushState(MACRO_ARGUMENTS); yypushState(MACRO_SIMPLE_FORM); return RpmSpecTypes.MACRO_START; }
%\{                                               { yypushState(MACRO_EXPANSION); return RpmSpecTypes.MACRO_START; }
%                                                 { yypushState(MACRO_SIMPLE_FORM); return RpmSpecTypes.MACRO_START; }
%description                                      { yybegin(DESCRIPTION); return RpmSpecTypes.BODY_ITEM; }
%changelog                                        { yybegin(CHANGELOG_HEADER); return RpmSpecTypes.BODY_ITEM; }
%package                                          { yybegin(PACKAGE_NAME); return RpmSpecTypes.BODY_ITEM; }
%(global|define|undefine)                         { yypushState(MACRO_DEFINITION_NAME); return RpmSpecTypes.KEYWORD; }
{COMMENT}                                         { return RpmSpecTypes.COMMENT; }
{COMMAND_SECTIONS}                                { yybegin(COMMAND_SECTION); return RpmSpecTypes.BODY_ITEM; }
({WHITE_SPACE}+|{CRLF}+)                          { return TokenType.WHITE_SPACE; }
[^]                                               { return TokenType.BAD_CHARACTER; }
