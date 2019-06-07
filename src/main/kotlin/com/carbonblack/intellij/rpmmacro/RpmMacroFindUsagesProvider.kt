package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroTypes
import com.intellij.lang.cacheBuilder.*
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.*
import com.intellij.psi.tree.TokenSet

class RpmMacroFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner? {
        return DefaultWordsScanner(RpmMacroLexerAdapter(),
                TokenSet.create(RpmMacroTypes.IDENTIFIER),
                TokenSet.create(RpmMacroTypes.COMMENT),
                TokenSet.EMPTY)
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is PsiNamedElement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        return if (element is RpmMacroMacro) {
            "macro"
        } else {
            ""
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        return if (element is RpmMacroMacro) {
            element.name
        } else {
            ""
        }
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return if (element is RpmMacroMacro) {
            element.name
        } else {
            ""
        }
    }
}
