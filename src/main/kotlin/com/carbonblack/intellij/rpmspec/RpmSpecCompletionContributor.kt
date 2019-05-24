package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.codeInsight.completion.*
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class RpmSpecCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(RpmSpecTypes.IDENTIFIER).withLanguage(RpmSpecLanguage.INSTANCE),
                object : CompletionProvider<CompletionParameters>() {
                    public override fun addCompletions(parameters: CompletionParameters,
                                                       context: ProcessingContext,
                                                       resultSet: CompletionResultSet) {
                        // Do nothing, just return references for now and don't add anything
                    }
                }
        )
    }

    // override fun invokeAutoPopup(position: PsiElement, typeChar: Char): Boolean =
    //         typeChar == '%'
}
