package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.vfs.VirtualFile

import javax.swing.*

object RpmMacroFileType : LanguageFileType(RpmMacroLanguage), FileTypeIdentifiableByVirtualFile {
    override fun getName(): String = "RPM Macro"
    override fun getDescription(): String = "RPM macro language file"
    override fun getDefaultExtension(): String = "rpmmacros"
    override fun getIcon(): Icon? = RpmMacroIcons.FILE

    override fun isMyFileType(file: VirtualFile): Boolean {
        return RpmMacroUtil.macroPathFiles.contains(file)
    }
}
