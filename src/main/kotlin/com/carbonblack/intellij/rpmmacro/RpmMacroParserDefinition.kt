package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.parser.RpmMacroParser
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroFile
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

private val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
private val COMMENTS = TokenSet.create(RpmMacroTypes.COMMENT)
private val FILE = IFileElementType(RpmMacroLanguage)

class RpmMacroParserDefinition : ParserDefinition {

    override fun createLexer(project: Project) = RpmMacroLexerAdapter()

    override fun getWhitespaceTokens() = WHITE_SPACES

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project) = RpmMacroParser()

    override fun getFileNodeType() = FILE

    override fun createFile(viewProvider: FileViewProvider) = RpmMacroFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) = ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode): PsiElement = RpmMacroTypes.Factory.createElement(node)
}
