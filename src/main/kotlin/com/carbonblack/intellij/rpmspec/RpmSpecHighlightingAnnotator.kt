package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.*
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class RpmSpecHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is RpmSpecMacro) {
            if (element.reference?.resolve() != null) {
                holder.createAnnotation(
                        HighlightSeverity.INFORMATION,
                        element.textRange,
                        null).textAttributes = RpmSpecSyntaxHighligher.MACRO_ITEM
            } else {
                holder.createAnnotation(
                        HighlightSeverity.WARNING,
                        element.textRange,
                        null).textAttributes = RpmSpecSyntaxHighligher.VERSION
            }
        } else if (element is RpmSpecDescriptionSection) {
            val body = element.genericSection.genericBody
            val bodyTextRange = element.genericSection.genericBody?.textRange
            if(body != null && bodyTextRange != null) {
                val startOffset = bodyTextRange.startOffset
                val endOffset = body.ifExprList.firstOrNull()?.textRange?.startOffset ?: bodyTextRange.endOffset
                holder.createAnnotation(
                        HighlightSeverity.INFORMATION,
                        TextRange(startOffset, endOffset),
                        null).textAttributes = RpmSpecSyntaxHighligher.TEXT
            }
        }

        val colorType = when (element) {
            is RpmSpecFullMacro -> RpmSpecSyntaxHighligher.BRACES
            is RpmSpecChangelogItem -> RpmSpecSyntaxHighligher.TEXT
            is RpmSpecTagValue -> RpmSpecSyntaxHighligher.VALUE
            is RpmSpecChangelogDate -> RpmSpecSyntaxHighligher.CHANGELOG_DATE
            is RpmSpecChangelogAuthor -> RpmSpecSyntaxHighligher.CHANGELOG_NAME
            is RpmSpecChangelogEmail -> RpmSpecSyntaxHighligher.CHANGELOG_EMAIL
            is RpmSpecChangelogVersion -> RpmSpecSyntaxHighligher.VERSION
            else -> null
        }

        if(colorType != null) {
            holder.createAnnotation(
                    HighlightSeverity.INFORMATION,
                    element.textRange,
                    null).textAttributes = colorType
        }
    }
}
