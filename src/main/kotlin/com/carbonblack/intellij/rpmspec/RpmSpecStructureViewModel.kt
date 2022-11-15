package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecFile
import com.intellij.ide.structureView.*
import com.intellij.psi.PsiFile

class RpmSpecStructureViewModel(psiFile: PsiFile) :
    StructureViewModelBase(psiFile, RpmSpecStructureViewElement(psiFile)),
    StructureViewModel.ElementInfoProvider {

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement) = element.value is RpmSpecFile
    override fun isAlwaysLeaf(element: StructureViewTreeElement) = element.value !is RpmSpecFile
}
