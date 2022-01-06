package com.carbonblack.intellij.rpmspec.shell;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import java.util.Stack;

import static com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecShellTypes.*;

%%

%class RpmSpecShellLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    eofReached = true;
%eof}

%{
    private Stack<Integer> stack = new Stack<>();
    private boolean eofReached = false;

    private void yypushState(int newState) {
      stack.push(yystate());
      yybegin(newState);
    }

    private void yypopState() {
      yybegin(stack.pop());
    }
%}

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
NON_ID_CHAR     = [^a-zA-Z0-9_\r\n]

IDENTIFIER      = {IDENTIFIER_CHAR}+

// The remainder of a line after a reserved word
RESERVED_LINE = ({NON_ID_CHAR} {INPUT_CHARACTER}*)?
// Reserved Words
IF_KEYWORDS=%(if|ifarch|ifnarch|ifos|ifnos)
ELSE_KEYWORDS=%(else|elifarch|elifos|elif)
ALL_IF_KEYWORDS={IF_KEYWORDS}|{ELSE_KEYWORDS}|%endif

SHELL_SECTIONS=%(prep|build|install|check|post|postun|posttrans|pre|preun|pretrans|clean)
SPEC_SECTIONS=%(description|files|changelog|package)

%state SHELL_SECTION
%state GLOBAL_SECTION
%state DEFINE_SECTION

%state MACRO_EXPANSION
%state SHELL_EXPANSION

%%

^{SHELL_SECTIONS} {RESERVED_LINE}         { yybegin(SHELL_SECTION); return SPEC_FILE; }
^{SPEC_SECTIONS} {RESERVED_LINE}          { yybegin(YYINITIAL); }
^{ALL_IF_KEYWORDS} {RESERVED_LINE}        { if( yystate() == SHELL_SECTION ) return SPEC_FILE; }

// TODO: There's a bug where starting without a whitespace character will color everything before this in orange.
// This is not a great workaround but until we figure out how to resolve it, it's better than the alternative.
"%(" [^\ \t\f\r\n]* {WHITE_SPACE_CHAR}    { yypushback(1); yypushState(SHELL_EXPANSION); return SPEC_FILE; }
// "%("                                   { yypushState(SHELL_EXPANSION); return SPEC_FILE; }

<MACRO_EXPANSION> {
  [^}%]+                          { } // { return SPEC_FILE; }
  "%"                             { } // { return SPEC_FILE; }
  %\{                             { yypushState(MACRO_EXPANSION); } // return SPEC_FILE; }
  \}                              { yypopState(); return SPEC_FILE; }
}

<SHELL_EXPANSION> {
  [^)%\"']+                       { } // { return SHELL_TEXT; }
  "%"                             { } // { return SHELL_TEXT; }
  \' [^\r\n\']+ \'                { } // { return SHELL_TEXT; }
  \" [^\r\n\"]+ \"                { } // { return SHELL_TEXT; }
  \" | \'                         { } // { return SHELL_TEXT; }
  \)                              { yypushback(1); yypopState(); return SHELL_TEXT; }
}

<SHELL_SECTION> {
  ([^%]|%%)+                      { return SHELL_TEXT; }
  "%" {IDENTIFIER}                { return SPEC_FILE; }
  %\{                             { yypushState(MACRO_EXPANSION); } // return SPEC_FILE; }
  "%%"                            { return SHELL_TEXT; }
}

<YYINITIAL> {
  "%"                             { } // { return SPEC_FILE; }
  [^%]+                           { } // { return SPEC_FILE; }
}

<GLOBAL_SECTION> {
  ([^%\r\n]|%%)+                  { } // { return SPEC_FILE; }
  "%"                             { } // { return SPEC_FILE; }
  {EOL}                           { yypushback(1); yypopState(); if( yystate() == SHELL_SECTION ) return SPEC_FILE; }
}

<DEFINE_SECTION> {
  ([^\}%]|%%)+                    { } // { return SPEC_FILE; }
  "%"                             { } // { return SPEC_FILE; }
  \}                              { yypopState(); if( yystate() == SHELL_SECTION ) return SPEC_FILE; }
}

%define {WHITE_SPACE} {IDENTIFIER} "(" ({IDENTIFIER_CHAR}|:)* ")" {WHITE_SPACE} "{"   { yypushState(DEFINE_SECTION); }
%global {WHITE_SPACE} {IDENTIFIER} {WHITE_SPACE}                                      { yypushState(GLOBAL_SECTION); }

[^]                               { return TokenType.BAD_CHARACTER; }
<<EOF>>                           { if (eofReached) { eofReached = false; return SPEC_FILE; } else { return null; } }
