package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
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

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is PsiNamedElement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        return when (element) {
            is RpmSpecMacro -> "macro"
            is RpmSpecTag -> "tag"
            else -> ""
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        return when (element) {
            is RpmSpecMacro -> element.name
            is RpmSpecTag -> element.text
            else -> ""
        }
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return when (element) {
            is RpmSpecMacro -> element.name
            is RpmSpecTag -> element.text
            else -> ""
        }
    }
}
