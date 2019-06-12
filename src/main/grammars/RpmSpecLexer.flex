package com.carbonblack.intellij.rpmspec;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes.*;

%%

%class RpmSpecLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}


// Whitespace
EOL              = \n | \r | \r\n
LINE_WS          = [\ \t\f]
WHITE_SPACE      = {LINE_WS}+

// Literals
INPUT_CHARACTER =  [^\r\n]

ALPHA           = [a-zA-Z]
NUMERIC         = [0-9]
IDENTIFIER_CHAR = {ALPHA}|{NUMERIC}|"_"
NON_ID_CHAR     = [^a-zA-Z0-9_]

IDENTIFIER      = {IDENTIFIER_CHAR}+
CODE_CHARS      = [^\r\n\ \t\f{}()%:?<>]+

INT_LITERAL   = {NUMERIC}+
FLT_LITERAL   = {INT_LITERAL} \. {INT_LITERAL}

COMMENT   =   "#" {INPUT_CHARACTER}*

// Reserved Words
IF_KEYWORDS=%(if|ifarch|ifnarch|ifos|ifnos)
ELSE_KEYWORDS=%(else|elifarch|elifos|elif)
FILES_DIRECTIVES=%(doc|config|attr|defattr|dir|docdir|ghost|verify|license|readme|exclude|pubkey|missingok|artifact)

TAGS=(Name|Summary|URL|Version|Release|License|Name|Summary|Requires|Provides|BuildRequires|Recommends|Obsoletes|Conflicts|Source\d*|Patch\d+)

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
  "<"                             { return LT; }
  ">"                             { return GT; }
  ^"*"                            { return CL_HEADER_TOKEN; }
  ^"-"                            { return CL_ITEM_TOKEN; }
  "-"                             { return MINUS; }

  ^{TAGS}                         { return PREAMBLE_TAG; }
  {COMMENT}                       { return COMMENT; }
  {FILES_DIRECTIVES}{NON_ID_CHAR} { yypushback(1); return FILES_DIRECTIVE_TOKEN; }

  {IF_KEYWORDS}{NON_ID_CHAR}      { yypushback(1); return IF; }
  {ELSE_KEYWORDS}{NON_ID_CHAR}    { yypushback(1); return ELSE; }
  "%endif"{NON_ID_CHAR}           { yypushback(1); return ENDIF; }

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
  ^"%setup"                       { return SETUP; }

  /* LITERALS */
  {FLT_LITERAL}                   { return FLOAT_LITERAL; }
  {INT_LITERAL}                   { return INTEGER_LITERAL; }

  {IDENTIFIER}                    { return IDENTIFIER; }

  {CODE_CHARS}                    { return CODE; }

  {EOL}                           { return EOL; }
  {WHITE_SPACE}                   { return TokenType.WHITE_SPACE; }
}

[^]                               { return TokenType.BAD_CHARACTER; }
