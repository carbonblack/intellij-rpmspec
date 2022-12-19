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
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import java.util.*
import java.util.concurrent.TimeUnit

private val log = Logger.getInstance(RpmSpecReference::class.java)

private val TAGS_WITH_MACROS = listOf(
    "name", "version", "release", "epoch", "summary", "license",
    "distribution", "disturl", "vendor", "group", "packager", "url", "vcs", "prefixes",
    "prefix", "disttag", "bugurl", "removepathpostfixes", "modularitylabel",
)

private val systemMacrosCache: LoadingCache<Pair<String, Project>, Optional<PsiElement>> = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build(
        object : CacheLoader<Pair<String, Project>, Optional<PsiElement>>() {
            override fun load(pair: Pair<String, Project>): Optional<PsiElement> {
                val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(pair.second))
                val rpmMacroFiles = virtualFiles.map { PsiManager.getInstance(pair.second).findFile(it) }
                for (file in rpmMacroFiles) {
                    val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == pair.first }
                    if (macros.isNotEmpty()) {
                        return Optional.of(macros.first())
                    }
                }
                return Optional.empty()
            }
        },
    )

class RpmSpecReference(element: PsiElement, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val definitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        val result: MutableList<PsiElement> = definitions.filter { it.name == key }.toMutableList()

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        result += virtualFiles.flatMap { virtualFile ->
            val file = PsiManager.getInstance(myElement.project).findFile(virtualFile)
            PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == key }
        }

        return result.map { PsiElementResolveResult(it) }.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        // Search local macros
        val definitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        val result = definitions.filter { it.name == key }
        if (result.isNotEmpty()) {
            return result.first()
        }

        // Search system macros
        val systemMacrosResult = try {
            systemMacrosCache.get(Pair(key, myElement.project)).orElse(null)
        } catch (e: UncheckedExecutionException) {
            log.warn("Error fetching from system macro cache", e)
            null
        } catch (e: ProcessCanceledException) {
            log.warn("Error fetching from system macro cache", e)
            null
        }
        if (systemMacrosResult != null) {
            return systemMacrosResult
        }

        // We might be a special macro mapped to a tag
        if (key.lowercase() in TAGS_WITH_MACROS ||
            key.lowercase().startsWith("patch") ||
            key.lowercase().startsWith("source")
        ) {
            val tags = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecTag::class.java)
            val filteredTags = tags.filter { it.firstChild.text.equals(key, ignoreCase = true) }
            return filteredTags.firstOrNull()
        }
        return null
    }

    override fun getVariants(): Array<LookupElementBuilder> {
        val variants = mutableMapOf<String?, LookupElementBuilder>()

        val macros = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacro::class.java)
        variants += macros.filter { it.name?.isNotEmpty() == true }.map {
            it.name to LookupElementBuilder.create(it)
                .withIcon(RpmSpecIcons.FILE)
                .withTypeText(it.containingFile.name)
        }

        val macroDefinitions = PsiTreeUtil.findChildrenOfType(myElement.containingFile, RpmSpecMacroDefinition::class.java)
        variants += macroDefinitions.filter { it.name?.isNotEmpty() == true }.map {
            it.name to LookupElementBuilder.create(it)
                .withIcon(RpmSpecIcons.FILE)
                .withTypeText(it.containingFile.name)
        }

        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(myElement.project))
        variants += virtualFiles.flatMap { virtualFile ->
            val file = PsiManager.getInstance(myElement.project).findFile(virtualFile)
            PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java)
                .filter { it.name.isNotEmpty() }
                .map {
                    it.name to LookupElementBuilder.create(it)
                        .withIcon(RpmSpecIcons.FILE)
                        .withTypeText(it.containingFile.name)
                }
        }

        return variants.values.toTypedArray()
    }
}
