package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.parser.RpmSpecParser
import com.carbonblack.intellij.rpmspec.psi.RpmSpecFile
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.lang.*
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.tree.*

class RpmSpecParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer = RpmSpecLexerAdapter()

    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project): PsiParser = RpmSpecParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = RpmSpecFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements =
            ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode): PsiElement = RpmSpecTypes.Factory.createElement(node)

    companion object {
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val COMMENTS = TokenSet.create(RpmSpecTypes.COMMENT)

        val FILE = IFileElementType(RpmSpecLanguage.INSTANCE)
    }
}
