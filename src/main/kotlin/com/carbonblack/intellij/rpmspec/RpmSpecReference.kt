package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmmacro.RpmMacroFileType
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacroDefinition
import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil

import kotlin.collections.ArrayList

class RpmSpecReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val definitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        val result : MutableList<PsiElement> = definitions.filter { it.macro.name == key }.map { it.macro }.toMutableList()

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        val rpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(myElement.project).findFile(it) }
        for (file in rpmMacroFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == key }
            result += macros
        }

        val results = result.map { PsiElementResolveResult(it) }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val definitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        val result  = definitions.filter { it.macro.name == key }.map { it.macro }
        if (result.isNotEmpty()) {
            return result.first()
        }

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        val rpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(myElement.project).findFile(it) }
        for (file in rpmMacroFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == key }
            if (macros.isNotEmpty()) {
                return macros.first()
            }
        }

        return null
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
