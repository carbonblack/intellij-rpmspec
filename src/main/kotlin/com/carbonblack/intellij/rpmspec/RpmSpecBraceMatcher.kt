package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType

private val PAIRS = arrayOf(
    BracePair(RpmSpecTypes.LBRACE, RpmSpecTypes.RBRACE, true),
    BracePair(RpmSpecTypes.LPAREN, RpmSpecTypes.RPAREN, true),
)

class RpmSpecBraceMatcher : PairedBraceMatcher {

    override fun getPairs() = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, tokenType: IElementType?): Boolean = (
        WHITE_SPACE === tokenType ||
            tokenType === RpmSpecTypes.RBRACE ||
            tokenType === RpmSpecTypes.RPAREN ||
            tokenType === RpmSpecTypes.EOL ||
            null == tokenType
        )

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset
}
