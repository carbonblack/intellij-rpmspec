package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmmacro.RpmMacroIcons
import com.carbonblack.intellij.rpmspec.psi.RpmSpecPackageBody
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.pom.Navigatable
import com.intellij.psi.NavigatablePsiElement

class RpmSpecStructureViewElement(val element: NavigatablePsiElement) : StructureViewTreeElement, Navigatable by element {

    override fun getPresentation(): ItemPresentation {
        return element.presentation ?: run {
            val presentationText = if (element is RpmSpecPackageBody) {
                "%package"
            } else {
                element.text.split("\n").firstOrNull() ?: ""
            }
            PresentationData(presentationText, null, RpmMacroIcons.FILE, null)
        }
    }

    override fun getChildren(): Array<TreeElement> {
        return element.children
            .mapNotNull { it as? NavigatablePsiElement }
            .map { RpmSpecStructureViewElement(it) }
            .toTypedArray()
    }

    override fun getValue() = element
}
