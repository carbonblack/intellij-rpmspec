package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.IndexableSetContributor

class RpmMacroIndexableSetContributor : IndexableSetContributor() {
    override fun getAdditionalRootsToIndex(): Set<VirtualFile> =
            listOfNotNull(
                    LocalFileSystem.getInstance().findFileByPath("/usr/lib/rpm"),
                    LocalFileSystem.getInstance().findFileByPath("/usr/lib/rpm/macros")).toSet()

    override fun getAdditionalProjectRootsToIndex(project: Project): Set<VirtualFile> =
            listOfNotNull(
                    LocalFileSystem.getInstance().findFileByPath("/usr/lib/rpm"),
                    LocalFileSystem.getInstance().findFileByPath("/usr/lib/rpm/macros")).toSet()
}
