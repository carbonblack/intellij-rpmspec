package com.carbonblack.intellij.rpmmacro

import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope

import kotlin.collections.ArrayList

class RpmMacroReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        var file = myElement.containingFile

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        for (vfile in virtualFiles) {
            if (vfile.name == "macros") {
                print("hi")
            }
        }

        /*val fileViewProvider = PsiManager.getInstance(myElement.project).findViewProvider(
                LocalFileSystem.getInstance().findFileByPath("/usr/lib/rpm/macros")!!)

        file = LanguageParserDefinitions.INSTANCE.forLanguage(RpmSpecLanguage).createFile(fileViewProvider)*/

        val macros = RpmMacroUtil.findMacroDefinitions(myElement.project, key).map { it.macro }
        //com.intellij.util.indexing.IndexableSetContributor
        //var test : PsiFile = RpmSpecFile()
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
