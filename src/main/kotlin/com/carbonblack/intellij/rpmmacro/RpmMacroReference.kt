package com.carbonblack.intellij.rpmmacro

import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

import kotlin.collections.ArrayList

class RpmMacroReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val macros = RpmMacroUtil.findMacros(myElement.project, key)
        val results = macros.map { PsiElementResolveResult(it) }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return resolveResults.firstOrNull()?.element
    }

    override fun getVariants(): Array<Any> {
        val file = myElement.containingFile
        val macros = RpmMacroUtil.findMacros(file)
        val variants = macros.filter { it.name.isNotEmpty() }.map {
            it.name to LookupElementBuilder.create(it)
                .withIcon(RpmMacroIcons.FILE)
                .withTypeText(it.containingFile.name) }.toMap()
        return ArrayList(variants.values).toTypedArray()
    }
}
