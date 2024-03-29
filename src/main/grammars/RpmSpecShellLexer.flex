package com.carbonblack.intellij.rpmspec.shell;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.carbonblack.intellij.rpmspec.shell.psi.RpmSpecShellTypes.*;

%%

%class RpmSpecShellLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    // do nothing
%eof}

%{
    private final Deque<Integer> stack = new ArrayDeque<>();
    private boolean firstIdentifierFound = false;

    private void yypushState(int newState) {
      stack.push(yystate());
      yybegin(newState);
    }

    private void yypopState() {
      yybegin(stack.pop());
    }

    private int getFirstScriptingParentState() {
        for(Integer state : stack) {
            if(state == SHELL_SECTION || state == LUA_SECTION) {
                return state;
            }
        }
        return 0;
    }

    private IElementType getWhiteSpace() {
        if (yystate() == SHELL_EXPANSION || yystate() == SHELL_SECTION) {
            return SHELL_WHITE_SPACE;
        } else if (yystate() == LUA_SECTION) {
            return LUA_WHITE_SPACE;
        } else {
            return SPEC_WHITE_SPACE;
        }
    }
%}

// Whitespace
EOL                   = \n | \r | \r\n
LINE_WS               = [\ \t\f]
WHITE_SPACE_CHAR      = {EOL} | {LINE_WS}
WHITE_SPACE           = {WHITE_SPACE_CHAR}+

// Literals
INPUT_CHARACTER =  [^\r\n]

ALPHA           = [a-zA-Z]
NUMERIC         = [0-9]
IDENTIFIER_CHAR = {ALPHA}|{NUMERIC}|"_"
NON_ID_CHAR     = [^a-zA-Z0-9_]
NON_ID_OR_EOL   = [^a-zA-Z0-9_\r\n]

IDENTIFIER      = {IDENTIFIER_CHAR}+

// The remainder of a line after a reserved word
RESERVED_LINE = ({NON_ID_OR_EOL} {INPUT_CHARACTER}*)?
// Reserved Words
IF_KEYWORDS=%(if|ifarch|ifnarch|ifos|ifnos)
ELSE_KEYWORDS=%(else|elifarch|elifos|elif)
ALL_IF_KEYWORDS={IF_KEYWORDS}|{ELSE_KEYWORDS}|%endif

// See: https://github.com/rpm-software-management/rpm/blob/rpm-4.17.0-release/build/parseSpec.c#L46-L80
// and: https://github.com/rpm-software-management/rpm/blob/rpm-4.17.0-release/build/parseSpec.c#L915
SHELL_SECTIONS=%(generate_buildrequires|prep|build|install|check|post|postun|posttrans|pre|preun|
                 pretrans|clean|verifyscript|triggerpostun|triggerprein|triggerun|triggerin|trigger|
                 filetriggerin|filetrigger|filetriggerun|filetriggerpostun|transfiletriggerin|transfiletrigger|
                 transfiletriggerun|transfiletriggerun|transfiletriggerpostun)
SPEC_SECTIONS=%(description|files|changelog|package)

%state SHELL_SECTION
%state GLOBAL_SECTION
%state DEFINE_SECTION
%state LUA_SECTION

%state SHELL_SECTION_HEADER

%state MACRO_EXPANSION
%state SHELL_EXPANSION

%%

^{SHELL_SECTIONS} {NON_ID_CHAR}             { yypushback(1); yybegin(SHELL_SECTION_HEADER); return SPEC_FILE; }
^{SPEC_SECTIONS} {RESERVED_LINE}            { yybegin(YYINITIAL); return SPEC_FILE; }
{ALL_IF_KEYWORDS} {RESERVED_LINE} {EOL}?    { return SPEC_FILE; }

"%("                              { yypushState(SHELL_EXPANSION); return SPEC_FILE; }

<SHELL_SECTION_HEADER> {
    [^\ \t\f\r\n<]+               { return SPEC_FILE; }
    "<lua>" {INPUT_CHARACTER}*    { yybegin(LUA_SECTION); return SPEC_FILE; }
    {EOL}                         { yypushback(1); yybegin(SHELL_SECTION); }
}

<MACRO_EXPANSION> {
  [^a-zA-Z0-9_\ \t\f\r\n}%]+      { return SPEC_FILE; }
  {IDENTIFIER}                    { if (!firstIdentifierFound) {
                                        firstIdentifierFound = true;
                                        if (getFirstScriptingParentState() == SHELL_SECTION) {
                                            return SPEC_FILE_MACRO_IDENTIFIER_SHELL;
                                        } else {
                                            return SPEC_FILE_MACRO_IDENTIFIER_LUA;
                                        }
                                    } else return SPEC_FILE; }
  "%"                             { return SPEC_FILE; }
  %\{                             { yypushState(MACRO_EXPANSION); firstIdentifierFound = false; return SPEC_FILE; }
  \}                              { yypopState(); return SPEC_FILE; }
}

<SHELL_EXPANSION> {
  [^)%\"\ \t\f\r\n']+             { return SHELL_TEXT; }
  "%"                             { return SHELL_TEXT; }
  \' [^\r\n\']+ \'                { return SHELL_TEXT; }
  \" [^\r\n\"]+ \"                { return SHELL_TEXT; }
  \" | \'                         { return SHELL_TEXT; }
  \)                              { yypopState(); return SPEC_FILE; }
}

<SHELL_SECTION, LUA_SECTION> {
  ([^\ \t\f\r\n%]|%%)+            { if (yystate() == SHELL_SECTION) return SHELL_TEXT; else return LUA_TEXT; }
  "%" {IDENTIFIER}                { if (yystate() == SHELL_SECTION) return SPEC_FILE_MACRO_SHELL; else return SPEC_FILE_MACRO_LUA; }
  %\{                             { yypushState(MACRO_EXPANSION); firstIdentifierFound = false; return SPEC_FILE; }
  "%%"                            { if (yystate() == SHELL_SECTION) return SHELL_TEXT; else return LUA_TEXT; }
}

<YYINITIAL> {
  "%"                             { return SPEC_FILE; }
  [^\ \t\f\r\n%]+                 { return SPEC_FILE; }
}

<GLOBAL_SECTION> {
  ([^\ \t\f\r\n%]|%%)+            { return SPEC_FILE; }
  "%"                             { return SPEC_FILE; }
  {EOL}                           { yypushback(1); yypopState(); return SPEC_FILE; }
}

<DEFINE_SECTION> {
  ([^\ \t\f\r\n}%]|%%)+           { return SPEC_FILE; }
  "%"                             { return SPEC_FILE; }
  \}                              { yypopState(); return SPEC_FILE; }
}

%define {WHITE_SPACE} {IDENTIFIER} "(" ({IDENTIFIER_CHAR}|:)* ")"   { yypushState(DEFINE_SECTION); return SPEC_FILE; }
%global {WHITE_SPACE} {IDENTIFIER}                                  { yypushState(GLOBAL_SECTION); return SPEC_FILE; }

{LINE_WS}+                        { return getWhiteSpace(); }
{EOL}                             { return getWhiteSpace(); }
[^]                               { return TokenType.BAD_CHARACTER; }
