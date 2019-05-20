package com.carbonblack.intellij.rpmspec

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.*

class RpmSpecFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val group = FoldingGroup.newGroup("simple")

        val descriptors = ArrayList<FoldingDescriptor>()
        val literalExpressions = PsiTreeUtil.findChildrenOfType(root, PsiLiteralExpression::class.java)
        for (literalExpression in literalExpressions) {
            val value = if (literalExpression.value is String) literalExpression.value as String? else null

            if (value != null && value.startsWith("simple:")) {
                val project = literalExpression.project
                val key = value.substring(7)
                val properties = RpmSpecUtil.findProperties(project, key)
                if (properties.size == 1) {
                    descriptors.add(object : FoldingDescriptor(literalExpression.node,
                            TextRange(literalExpression.textRange.startOffset + 1,
                                    literalExpression.textRange.endOffset - 1),
                            group) {
                        override fun getPlaceholderText(): String? {
                            // IMPORTANT: keys can come with no values, so a test for null is needed
                            // IMPORTANT: Convert embedded \n to backslash n, so that the string will look like it has LF embedded
                            // in it and embedded " to escaped "
                            val valueOf = properties[0].value
                            return valueOf?.replace("\n".toRegex(), "\\n")?.replace("\"".toRegex(), "\\\\\"") ?: ""
                        }
                    })
                }
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return true
    }
}
