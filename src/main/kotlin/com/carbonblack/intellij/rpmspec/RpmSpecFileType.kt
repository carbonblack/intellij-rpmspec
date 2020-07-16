package com.carbonblack.intellij.rpmspec

import com.intellij.openapi.fileTypes.LanguageFileType

object RpmSpecFileType : LanguageFileType(RpmSpecLanguage) {
    override fun getName() = "RPM SPEC"
    override fun getDescription() = "RPM SPEC language file"
    override fun getDefaultExtension() = "spec"
    override fun getIcon() = RpmSpecIcons.FILE
}
