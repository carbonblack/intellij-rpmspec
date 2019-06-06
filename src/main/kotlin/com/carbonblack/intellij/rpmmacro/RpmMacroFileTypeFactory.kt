package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.fileTypes.*

class RpmMacroFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(fileTypeConsumer: FileTypeConsumer) {
        fileTypeConsumer.consume(RpmMacroFileType)
    }
}
