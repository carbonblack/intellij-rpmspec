package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.util.io.ByteSequence
import com.intellij.openapi.vfs.VirtualFile

class RpmMacroFileTypeDetector : FileTypeRegistry.FileTypeDetector {
    override fun getVersion(): Int = 1

    override fun detect(file: VirtualFile, firstBytes: ByteSequence, firstCharsIfText: CharSequence?): FileType? =
            if (file.name == "macros" && file.parent?.name == "rpm") RpmMacroFileType else null
}
