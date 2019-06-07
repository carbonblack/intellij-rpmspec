package com.carbonblack.intellij.rpmmacro

import com.intellij.openapi.fileTypes.LanguageFileType

import javax.swing.*

object RpmMacroFileType : LanguageFileType(RpmMacroLanguage) {

    override fun getName(): String = "RPM Macro"

    override fun getDescription(): String = "RPM macro language file"

    override fun getDefaultExtension(): String = "rpmmacros"

    override fun getIcon(): Icon? = RpmMacroIcons.FILE
}
