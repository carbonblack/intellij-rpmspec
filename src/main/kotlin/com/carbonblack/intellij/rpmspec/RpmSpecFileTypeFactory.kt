package com.carbonblack.intellij.rpmspec

import com.intellij.openapi.fileTypes.*

class RpmSpecFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(fileTypeConsumer: FileTypeConsumer) {
        fileTypeConsumer.consume(RpmSpecFileType)
    }
}
