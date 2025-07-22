package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

private val KNOWN_TAGS = listOf(
    "name", "version", "release", "epoch", "summary", "license",
    "distribution", "disturl", "vendor", "group", "packager", "url",
    "vcs", "source", "patch", "nosource", "nopatch", "excludearch", "exclusivearch",
    "excludeos", "exclusiveos", "icon", "provides", "requires", "recommends", "suggests",
    "supplements", "enhances", "prereq", "conflicts", "obsoletes", "prefixes", "prefix",
    "buildroot", "buildarchitectures", "buildarch", "buildconflicts", "buildprereq",
    "buildrequires", "autoreqprov", "autoreq", "autoprov", "docdir", "disttag", "bugurl",
    "orderwithrequires", "removepathpostfixes", "modularitylabel",
)

abstract class RpmSpecTagElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    RpmSpecTagElement {

    override fun getNameIdentifier() = node.findChildByType(RpmSpecTypes.IDENTIFIER)?.psi

    override fun setName(name: String): PsiElement {
        node.findChildByType(RpmSpecTypes.IDENTIFIER)?.let {
            val macro = RpmSpecElementFactory.createTag(project, name)
            val newKeyNode = macro.firstChild.node
            node.replaceChild(it, newKeyNode)
        }
        return this
    }

    override fun getName() = node.findChildByType(RpmSpecTypes.IDENTIFIER)?.text

    override fun getReference() = RpmSpecReference(this, TextRange(0, name?.length ?: 0))

    override fun knownTag() = name?.let {
        it.lowercase() in KNOWN_TAGS ||
            "source\\d*".toRegex().matches(it.lowercase()) ||
            "patch\\d+".toRegex().matches(it.lowercase())
    } ?: false
}
