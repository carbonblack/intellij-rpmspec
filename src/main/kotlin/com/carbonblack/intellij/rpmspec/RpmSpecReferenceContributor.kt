package com.carbonblack.intellij.rpmspec

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class RpmSpecReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement,
                                                        context: ProcessingContext): Array<PsiReference> {
                        val literalExpression = element as PsiLiteralExpression
                        val value = if (literalExpression.value is String)
                            literalExpression.value as String?
                        else
                            null
                        return if (value != null) {
                            arrayOf(RpmSpecReference(element, TextRange(0, value.length)))
                        } else PsiReference.EMPTY_ARRAY
                    }
                })
    }
}
