package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.*
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet

class RpmSpecHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is RpmSpecMacro -> {
                if (element.reference?.resolve() == null) {
                    holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Macro could not be resolved")
                            .range(element.textRange).create()
                }
            }
            is RpmSpecDescriptionSection -> {
                val body = element.genericSection.genericBody
                val bodyTextRange = element.genericSection.genericBody?.textRange
                if(body != null && bodyTextRange != null) {
                    val startOffset = bodyTextRange.startOffset
                    val endOffset = body.ifExprList.firstOrNull()?.textRange?.startOffset ?: bodyTextRange.endOffset
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.TEXT)
                            .range(TextRange(startOffset, endOffset)).create()
                }
            }
            is RpmSpecMacroDefinition -> element.node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.MACRO_ITEM)
                        .range(it.textRange).create()
            }
            is RpmSpecTag -> element.node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.KEY)
                        .range(it.textRange).create()
                if (!element.knownTag()) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Unknown tag: \"${it.text}\"")
                            .range(it.textRange).create()
                }
            }
            is RpmSpecMultilineMacro -> element.node.getChildren(TokenSet.ANY).forEach {
                if (it.elementType == RpmSpecTypes.COMMENT) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.COMMENT)
                            .range(it.textRange).create()
                }
            }
            is RpmSpecIfExpr -> element.node.getChildren(TokenSet.ANY).forEach {
                if (it.elementType == RpmSpecTypes.IF) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.RESERVED)
                            .range(it.textRange).create()
                }
            }
            is RpmSpecElseBranch -> element.node.getChildren(TokenSet.ANY).forEach {
                if (it.elementType == RpmSpecTypes.ELSE) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.RESERVED)
                            .range(it.textRange).create()
                }
            }
            is RpmSpecEndIfExpr -> element.node.getChildren(TokenSet.ANY).forEach {
                if (it.elementType == RpmSpecTypes.ENDIF) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.RESERVED)
                            .range(it.textRange).create()
                }
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
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(it)
                    .range(element.textRange).create()
        }
    }
}
