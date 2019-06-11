package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecIfExpr
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.*

class RpmSpecFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = ArrayList<FoldingDescriptor>()
        val ifStatements = PsiTreeUtil.findChildrenOfType(root, RpmSpecIfExpr::class.java)
        for (ifStatement in ifStatements) {
            val startOffset = ifStatement.node.findChildByType(RpmSpecTypes.EOL)?.textRange?.startOffset
            if (startOffset != null) {
                descriptors.add(object : FoldingDescriptor(ifStatement.node,
                        TextRange(startOffset,
                                ifStatement.textRange.endOffset),
                        FoldingGroup.newGroup("rpm-spec-if-statement")) {
                    override fun getPlaceholderText(): String? {
                        return "...${ifStatement.endIfExpr?.text ?: ""}"
                    }
                })
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }
}
