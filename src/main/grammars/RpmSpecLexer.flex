package com.carbonblack.intellij.rpmspec;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import java.util.Stack;

import static com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes.*;

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


///////////////////////////////////////////////////////////////////////////////////////////////////
// Whitespaces
///////////////////////////////////////////////////////////////////////////////////////////////////

EOL              = \n | \r | \r\n
LINE_WS          = [\ \t\f]
WHITE_SPACE      = {LINE_WS}+


INPUT_CHARACTER =  [^\r\n]

///////////////////////////////////////////////////////////////////////////////////////////////////
// Identifier
///////////////////////////////////////////////////////////////////////////////////////////////////

ALPHA           = [a-zA-Z]
NUMERIC         = [0-9]
IDENTIFIER_CHAR = {ALPHA}|{NUMERIC}|"_"

IDENTIFIER      = {IDENTIFIER_CHAR}+
SUFFIX          = {IDENTIFIER}
CODE            = [^\r\n\ \t\f{}()%:?]+


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

IF_KEYWORDS=(if|ifarch|ifnarch|ifos|ifnos)
ELSE_KEYWORDS=(else|elifarch|elifos|elif)

KEYWORDS=%(if|else|endif|ifarch)
TAGS=(Name|Summary|URL|Version|Release|License|Name|Summary|Requires|Provides|BuildRequires|Recommends|Obsoletes|Conflicts|Source\d*|Patch\d+)

%state INITIAL

%%


<YYINITIAL> {
  "{"                             { return LBRACE; }
  "}"                             { return RBRACE; }
  "("                             { return LPAREN; }
  ")"                             { return RPAREN; }
  ":"                             { return COLON; }
  "%"                             { return PERCENT; }
  "%%"                            { return ESCAPED_PERCENT; }
  "?"                             { return QUESTION_MARK; }

  //{KEYWORDS}                    { return RESERVED_KEYWORD; }
  ^{TAGS}                         { return PREAMBLE_TAG; }
  {COMMENT}                       { return COMMENT; }

  ^%{IF_KEYWORDS}                 { return IF; }
  ^%{ELSE_KEYWORDS}               { return ELSE; }
  ^"%endif"                       { return ENDIF; }

  "true"|"false"                  { return BOOL_LITERAL; }

  ^"%prep"                        { return PREP; }
  ^"%build"                       { return BUILD; }
  ^"%install"                     { return INSTALL; }
  ^"%check"                       { return CHECK; }
  ^"%files"                       { return FILES; }
  ^"%post"                        { return POST; }
  ^"%postun"                      { return POSTUN; }
  ^"%posttrans"                   { return POSTTRANS; }
  ^"%pre"                         { return PRE; }
  ^"%preun"                       { return PREUN; }
  ^"%pretrans"                    { return PRETRANS; }
  ^"%clean"                       { return CLEAN; }
  ^"%description"                 { return DESCRIPTION; }
  ^"%changelog"                   { return CHANGELOG; }
  ^"%package"                     { return PACKAGE; }
  ^"%global"                      { return GLOBAL; }
  ^"%define"                      { return DEFINE; }
  ^"%undefine"                    { return UNDEFINE; }

  {IDENTIFIER}                    { return IDENTIFIER; }

  /* LITERALS */

  // Floats must come first, to parse 1e1 as a float and not as an integer with a suffix
  {FLT_LITERAL}                   { return FLOAT_LITERAL; }

  {INT_LITERAL}                   { return INTEGER_LITERAL; }

  {CODE}                          { return CODE; }

  {EOL}                           { return EOL; }
  {WHITE_SPACE}                   { return TokenType.WHITE_SPACE; }
}

[^]                               { return TokenType.BAD_CHARACTER; }
