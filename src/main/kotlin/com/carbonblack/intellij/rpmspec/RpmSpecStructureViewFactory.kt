package com.carbonblack.intellij.rpmspec

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class RpmSpecStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder = object : TreeBasedStructureViewBuilder() {
        override fun createStructureViewModel(editor: Editor?) = RpmSpecStructureViewModel(psiFile)
    }
}
