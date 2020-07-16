package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.parser.RpmSpecParser
import com.carbonblack.intellij.rpmspec.psi.RpmSpecFile
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.lang.*
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.tree.*

private val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
private val COMMENTS = TokenSet.create(RpmSpecTypes.COMMENT)
private val FILE = IFileElementType(RpmSpecLanguage)

class RpmSpecParserDefinition : ParserDefinition {

    override fun createLexer(project: Project) = RpmSpecLexerAdapter()

    override fun getWhitespaceTokens() = WHITE_SPACES

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project) = RpmSpecParser()

    override fun getFileNodeType() = FILE

    override fun createFile(viewProvider: FileViewProvider) = RpmSpecFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) =
            ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode): PsiElement = RpmSpecTypes.Factory.createElement(node)
}
