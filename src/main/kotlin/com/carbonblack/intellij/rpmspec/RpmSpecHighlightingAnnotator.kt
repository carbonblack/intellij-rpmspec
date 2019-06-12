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
            if (element.reference?.resolve() == null) {
                holder.createWeakWarningAnnotation(
                        element.textRange,
                        "Macro could not be resolved")
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
        } else if (element is RpmSpecMacroDefinition) {
            element.node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let {
                holder.createAnnotation(
                        HighlightSeverity.INFORMATION,
                        it.textRange,
                        null).textAttributes = RpmSpecSyntaxHighligher.MACRO_ITEM
            }
        }

        val colorType = when (element) {
            is RpmSpecMacro -> RpmSpecSyntaxHighligher.MACRO_ITEM
            is RpmSpecFullMacro -> RpmSpecSyntaxHighligher.BRACES
            is RpmSpecShellCommand -> RpmSpecSyntaxHighligher.BRACES
            is RpmSpecChangelogItem -> RpmSpecSyntaxHighligher.TEXT
            is RpmSpecChangelogEntry -> RpmSpecSyntaxHighligher.TEXT
            is RpmSpecTagValue -> RpmSpecSyntaxHighligher.VALUE
            is RpmSpecChangelogDate -> RpmSpecSyntaxHighligher.CHANGELOG_DATE
            is RpmSpecChangelogAuthor -> RpmSpecSyntaxHighligher.CHANGELOG_NAME
            is RpmSpecChangelogEmail -> RpmSpecSyntaxHighligher.CHANGELOG_EMAIL
            is RpmSpecChangelogVersion -> RpmSpecSyntaxHighligher.VERSION
            else -> null
        }

        colorType?.let {
            holder.createAnnotation(
                    HighlightSeverity.INFORMATION,
                    element.textRange,
                    null).textAttributes = it
        }
    }
}
