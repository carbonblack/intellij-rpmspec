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

STRING = \'(\\.|[^\'\\])*\'|\"(\\.|[^\"\\])*\"
CRLF = \n | \r | \r\n
WHITE_SPACE_CHAR = [\ \n\r\t\f]
OPENING = "%{"
CLOSING = "}"
MACRO_NAME = [^\'\"{} ]+



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
CODE_CHARS      = [^\r\n\ \t\f{}()%:?\\]+

INT_LITERAL   = {NUMERIC}+
FLT_LITERAL   = {INT_LITERAL} \. {INT_LITERAL}

COMMENT       = "#" {INPUT_CHARACTER}*
MACRO_ESCAPE  = "\\"{WHITE_SPACE}*{EOL}

// The remainder of a line after a reserved word
RESERVED_LINE = ([^a-zA-Z0-9_\r\n] {INPUT_CHARACTER}*)?
// Reserved Words
IF_KEYWORDS=%(if|ifarch|ifnarch|ifos|ifnos)
ELSE_KEYWORDS=%(else|elifarch|elifos|elif)
ALL_IF_KEYWORDS={IF_KEYWORDS}|{ELSE_KEYWORDS}|%endif

SHELL_SECTIONS=%(prep|build|install|check|post|postun|posttrans|pre|preun|pretrans|clean)
SPEC_SECTIONS=%(description|files|changelog|package)

%state MACRO
%state SHELL_SECTION
%state MACRO_EXPANSION

%%

^{SHELL_SECTIONS} {RESERVED_LINE}         { yybegin(SHELL_SECTION); return SPEC_FILE; }
^{SPEC_SECTIONS} {RESERVED_LINE}          { yybegin(YYINITIAL); return SPEC_FILE; }
^{ALL_IF_KEYWORDS} {RESERVED_LINE}        { return SPEC_FILE; }


<MACRO_EXPANSION> {
  [^}%]+                          {} //{ return SPEC_FILE; }
  "%"                             {} //{ return SPEC_FILE; }
  %\{                             { yypushState(MACRO_EXPANSION); } // return SPEC_FILE; }
  \}                              { yypopState(); return SPEC_FILE; }
}

<SHELL_SECTION> {
  [^%]+                           { return SHELL_TEXT; }
  "%"                             { return SPEC_FILE; }
  "%" {IDENTIFIER}                { return SPEC_FILE; }
  %\{                             { yypushState(MACRO_EXPANSION); } // return SPEC_FILE; }
  "%%"                            { return SHELL_TEXT; }
}

<YYINITIAL> {
  "%"                             { } //{ return SPEC_FILE; }
  [^%]+                           { return SPEC_FILE; }
}

<MACRO> {
    [^\}]+                        { return SPEC_FILE; }
    "}"                           { yybegin(YYINITIAL); return SPEC_FILE; }
}


%define {WHITE_SPACE} {IDENTIFIER} \(({IDENTIFIER_CHAR}|:)*\) {WHITE_SPACE} \{[^\}]*\}    { return SPEC_FILE; }
%global {WHITE_SPACE} {IDENTIFIER} {WHITE_SPACE} {INPUT_CHARACTER}*                       { return SPEC_FILE; }

[^]                               { return TokenType.BAD_CHARACTER; }
