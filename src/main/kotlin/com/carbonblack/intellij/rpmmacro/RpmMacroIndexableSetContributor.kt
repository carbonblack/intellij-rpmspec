package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.IndexableSetContributor

class RpmMacroIndexableSetContributor : IndexableSetContributor() {
    override fun getAdditionalRootsToIndex(): Set<VirtualFile> = RpmMacroUtil.macroPathFiles
}
