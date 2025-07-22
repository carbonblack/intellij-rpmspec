package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.shell.RpmSpecEditorHighlighter
import com.intellij.openapi.fileTypes.EditorHighlighterProvider
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders
import com.intellij.openapi.fileTypes.LanguageFileType

object RpmSpecFileType : LanguageFileType(RpmSpecLanguage) {
    init {
        FileTypeEditorHighlighterProviders.getInstance().addExplicitExtension(
            this,
            EditorHighlighterProvider { project, _, virtualFile, colors ->
                RpmSpecEditorHighlighter(
                    project,
                    virtualFile,
                    colors,
                )
            },
        )
    }

    override fun getName() = "RPM SPEC"
    override fun getDescription() = "RPM SPEC language file"
    override fun getDefaultExtension() = "spec"
    override fun getIcon() = RpmSpecIcons.FILE
}
