package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmmacro.RpmMacroFileType
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacroDefinition
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTag
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.util.concurrent.UncheckedExecutionException
import com.intellij.codeInsight.lookup.*
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import java.util.*
import java.util.concurrent.TimeUnit

import kotlin.collections.ArrayList

class RpmSpecReference(element: PsiElement, textRange: TextRange) :
        PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val definitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        val result : MutableList<PsiElement?> = definitions.filter { it.name == key }.toMutableList()

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        val rpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(myElement.project).findFile(it) }
        for (file in rpmMacroFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == key }
            result += macros
        }

        val results = result.filterNotNull().map { PsiElementResolveResult(it) }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        // Search local macros
        val definitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        val result  = definitions.filter { it.name == key }
        if (result.isNotEmpty()) {
            return result.first()
        }

        // Search system macros
        val systemMacrosResult = try {
            systemMacrosCache.get(Pair(key, myElement.project)).orElse(null)
        } catch (e: UncheckedExecutionException) {
            null
        } catch (e: ProcessCanceledException) {
            null
        }
        if (systemMacrosResult != null) {
            return systemMacrosResult
        }

        // We might be a special macro mapped to a tag
        if (key.toLowerCase() in tagsWithMacros
                || key.toLowerCase().startsWith("patch")
                || key.toLowerCase().startsWith("source")) {
            val tags = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecTag::class.java)
            val filteredTags = tags.filter { it.firstChild.text.toLowerCase() == key.toLowerCase() }
            return filteredTags.firstOrNull()
        }
        return null
    }

    override fun getVariants(): Array<LookupElementBuilder> {
        val macros = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacro::class.java)
        val variants : MutableMap<String?, LookupElementBuilder> = mutableMapOf()
        variants += macros.filter { it.name?.isNotEmpty() ?: false }.map {
            it.name to LookupElementBuilder.create(it)
                .withIcon(RpmSpecIcons.FILE)
                .withTypeText(it.containingFile.name) }

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        val rpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(myElement.project).findFile(it) }
        for (file in rpmMacroFiles) {
            variants += PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java)
                    .filter { it.name.isNotEmpty() }
                    .map {
                        it.name to LookupElementBuilder.create(it)
                                .withIcon(RpmSpecIcons.FILE)
                                .withTypeText(it.containingFile.name) }
        }

        return ArrayList(variants.values).toTypedArray()
    }

    companion object {
        val tagsWithMacros = listOf("name", "version", "release", "epoch", "summary", "license",
                "distribution", "disturl", "vendor", "group", "packager", "url", "vcs", "prefixes",
                "prefix", "disttag", "bugurl", "removepathpostfixes", "modularitylabel")

        val systemMacrosCache: LoadingCache<Pair<String, Project>, Optional<PsiElement>> = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(object : CacheLoader<Pair<String, Project>, Optional<PsiElement>>() {
                    override fun load(pair: Pair<String, Project>): Optional<PsiElement> {
                        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(pair.second))
                        val rpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(pair.second).findFile(it) }
                        for (file in rpmMacroFiles) {
                            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == pair.first }
                            if (macros.isNotEmpty()) {
                                return Optional.of(macros.first())
                            }
                        }
                        return Optional.empty()
                    }
                })
    }
}
