package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class RpmMacroReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(RpmSpecMacro::class.java),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement,
                                                        context: ProcessingContext): Array<PsiReference> {
                        val literalExpression = element as RpmSpecMacro
                        val value = literalExpression.text

                        return if (value.isNotBlank()) {
                            arrayOf(RpmMacroReference(element, TextRange(0, value.length)))
                        } else PsiReference.EMPTY_ARRAY
                    }
                })
    }
}
