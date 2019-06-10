package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class RpmMacroHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val identifier = (element as? RpmMacroMacro)?.nameIdentifier
        if (identifier != null) {
            holder.createAnnotation(HighlightSeverity.INFORMATION, identifier.textRange, null).textAttributes = RpmMacroSyntaxHighligher.MACRO_ITEM
        }
    }
}
