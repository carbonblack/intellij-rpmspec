package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacroDefinition
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTag
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.lang.cacheBuilder.*
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.*
import com.intellij.psi.tree.TokenSet

class RpmSpecFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner? {
        return DefaultWordsScanner(RpmSpecLexerAdapter(),
                TokenSet.create(RpmSpecTypes.IDENTIFIER),
                TokenSet.create(RpmSpecTypes.COMMENT),
                TokenSet.EMPTY)
    }

    override fun canFindUsagesFor(psiElement: PsiElement) = psiElement is PsiNamedElement

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement) = when (element) {
        is RpmSpecMacro -> "macro"
        is RpmSpecMacroDefinition -> "macro definition"
        is RpmSpecTag -> "tag"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement) = when (element) {
        is RpmSpecMacro -> element.name ?: ""
        is RpmSpecMacroDefinition -> element.name ?: ""
        is RpmSpecTag -> element.name ?: ""
        else -> ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean) = when (element) {
        is RpmSpecMacro -> element.name ?: ""
        is RpmSpecMacroDefinition -> element.name ?: ""
        is RpmSpecTag -> element.name ?: ""
        else -> ""
    }
}
