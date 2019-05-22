package com.carbonblack.intellij.rpmspec

import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

import kotlin.collections.ArrayList

class RpmSpecReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val file = myElement.containingFile
        val macros = RpmSpecUtil.findMacros(file, key)
        val results = macros.map { PsiElementResolveResult(it) }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return resolveResults.firstOrNull()?.element
    }

    override fun getVariants(): Array<Any> {
        val file = myElement.containingFile
        val macros = RpmSpecUtil.findMacros(file)
        val variants = macros.filter { it.name.isNotEmpty() }.map {
            it.name to LookupElementBuilder.create(it)
                .withIcon(RpmSpecIcons.FILE)
                .withTypeText(it.containingFile.name) }.toMap()
        return ArrayList(variants.values).toTypedArray()
    }
}
