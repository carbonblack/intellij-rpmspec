package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.vfs.VirtualFile

object RpmMacroFileType : LanguageFileType(RpmMacroLanguage), FileTypeIdentifiableByVirtualFile {
    override fun getName() = "RPM Macro"
    override fun getDescription() = "RPM macro language file"
    override fun getDefaultExtension() = "rpmmacros"
    override fun getIcon() = RpmMacroIcons.FILE

    override fun isMyFileType(file: VirtualFile): Boolean = RpmMacroUtil.macroPathFiles.contains(file)
}
