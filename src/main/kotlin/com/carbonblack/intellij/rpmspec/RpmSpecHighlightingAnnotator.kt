package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.*
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentsOfType
import com.intellij.psi.util.startOffset

class RpmSpecHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is RpmSpecMacro -> {
                if (element.parentsOfType<RpmSpecFullMacro>().any { it.isFalseCondition }) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.COMMENT)
                        .range(element.textRange).create()
                } else if (!element.isBuiltInMacro && element.reference?.resolve() == null) {
                    if (element.parentOfType<RpmSpecMacroUndefine>() != null ||
                        (element.parent as? RpmSpecFullMacro)?.isConditionalMacro == true
                    ) {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.COMMENT)
                            .range(element.textRange).create()
                    } else {
                        holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Macro could not be resolved")
                            .range(element.textRange).create()
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.MACRO_ITEM)
                            .range(element.textRange).create()
                    }
                } else {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.MACRO_ITEM)
                        .range(element.textRange).create()
                }
            }
            is RpmSpecDescriptionSection -> {
                element.genericSection.children.filterIsInstance<RpmSpecGenericBody>().forEach { body ->
                    val bodyTextRange = body.textRange
                    if (bodyTextRange != null) {
                        val startOffset = bodyTextRange.startOffset
                        val endOffset = body.ifExprList.firstOrNull()?.textRange?.startOffset ?: bodyTextRange.endOffset
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.TEXT)
                            .range(TextRange(startOffset, endOffset)).create()
                    }
                }
            }
            is RpmSpecMacroDefinition -> element.node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let { macroDefinitionElement ->
                if (element.parentsOfType<RpmSpecFullMacro>(false).none { it.isFalseCondition }) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.MACRO_ITEM)
                        .range(macroDefinitionElement.textRange).create()
                }
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
            is RpmSpecFullMacro -> {
                if (element.parentsOfType<RpmSpecFullMacro>(false).any { it.isFalseCondition }) {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.COMMENT)
                        .range(element.textRange).create()
                } else {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.BRACES)
                        .range(
                            TextRange(
                                element.textRange.startOffset,
                                element.textRange.startOffset + (element.macro?.startOffsetInParent ?: 1),
                            ),
                        ).create()

                    element.macroBodySeparator?.let { separator ->
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.BRACES)
                            .range(separator.textRange).create()
                    }

                    if (element.text.last() == '}') {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.BRACES)
                            .range(TextRange(element.textRange.endOffset - 1, element.textRange.endOffset)).create()
                    }
                }

                element.macroBody?.let { body ->
                    if (element.isFalseCondition) {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(RpmSpecSyntaxHighligher.COMMENT)
                            .range(body.textRange).create()
                    }
                }
            }
            is RpmSpecShellCommand -> {
                listOf(
                    TextRange(element.startOffset, (element.startOffset + 2).coerceAtMost(element.endOffset)),
                    TextRange((element.endOffset - 1).coerceAtLeast(element.startOffset), element.endOffset),
                ).forEach { range ->
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.BRACES)
                        .range(range).create()
                }
            }
        }

        // Explicitly highlight the section headers; this resolves issues with the shell sections
        // causing the headers to be un-highlighted.
        when (element) {
            is RpmSpecFilesSection,
            is RpmSpecPackageSection,
            is RpmSpecDescriptionSection,
            is RpmSpecChangelogSection,
            -> element.node.getChildren(TokenSet.ANY)
                .firstOrNull {
                    it.elementType == RpmSpecTypes.FILES ||
                        it.elementType == RpmSpecTypes.PACKAGE ||
                        it.elementType == RpmSpecTypes.CHANGELOG ||
                        it.elementType == RpmSpecTypes.DESCRIPTION
                }?.let {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.RESERVED)
                        .range(it.textRange).create()
                }
            is RpmSpecGenericSection -> element.node.getChildren(TokenSet.ANY).firstOrNull()?.let {
                if (element.text.firstOrNull() == '%') {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(RpmSpecSyntaxHighligher.RESERVED)
                        .range(it.textRange).create()
                }
            }
        }

        val colorType = when (element) {
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
