package com.carbonblack.intellij.rpmspec

import com.intellij.openapi.fileTypes.LanguageFileType

import javax.swing.*

class RpmSpecFileType private constructor() : LanguageFileType(RpmSpecLanguage.INSTANCE) {

    override fun getName(): String = "RPM SPEC"

    override fun getDescription(): String = "RPM SPEC language file"

    override fun getDefaultExtension(): String = "spec"

    override fun getIcon(): Icon? = RpmSpecIcons.FILE

    companion object {
        val INSTANCE = RpmSpecFileType()
    }
}
