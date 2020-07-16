package com.carbonblack.intellij.rpmmacro

import com.intellij.util.indexing.IndexableSetContributor

class RpmMacroIndexableSetContributor : IndexableSetContributor() {
    override fun getAdditionalRootsToIndex() = RpmMacroUtil.macroPathFiles
}
