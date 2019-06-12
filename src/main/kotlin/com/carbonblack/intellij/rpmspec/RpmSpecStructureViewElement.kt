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
        val presentation = element.presentation
        if (presentation != null) {
            return presentation
        }

        val presentationText = if (element is RpmSpecPackageBody) {
            "%package"
        } else {
            element.text.split("\n").firstOrNull() ?: ""
        }
        return PresentationData(presentationText, null, RpmMacroIcons.FILE, null)
    }

    override fun getChildren(): Array<TreeElement> {
        val properties = element.children
        val treeElements = mutableListOf<TreeElement>()
        for (property in properties) {
            if (property is NavigatablePsiElement) {
                treeElements.add(RpmSpecStructureViewElement(property))
            }
        }
        return treeElements.toTypedArray()
    }

    override fun getValue(): Any {
        return element
    }
}
