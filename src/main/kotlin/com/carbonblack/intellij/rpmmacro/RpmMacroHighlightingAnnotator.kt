package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class RpmMacroHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        (element as? RpmMacroMacro)?.nameIdentifier?.let { identifier ->
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .textAttributes(RpmMacroSyntaxHighlighter.MACRO_ITEM)
                .range(identifier.textRange)
                .create()
        }
    }
}
