package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacroElement
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class RpmSpecCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(RpmSpecTypes.IDENTIFIER).withLanguage(RpmSpecLanguage),
            object : CompletionProvider<CompletionParameters>() {
                public override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    resultSet: CompletionResultSet,
                ) {
                    resultSet.addAllElements(
                        RpmSpecMacroElement.builtInMacros.map {
                            LookupElementBuilder.create(it).withTypeText("built-in macro")
                        },
                    )
                }
            },
        )
    }

    // override fun invokeAutoPopup(position: PsiElement, typeChar: Char): Boolean =
    //         typeChar == '%'
}
