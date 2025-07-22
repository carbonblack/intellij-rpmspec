package com.carbonblack.intellij.rpmmacro.psi

import com.carbonblack.intellij.rpmspec.RpmSpecFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory

object RpmMacroElementFactory {
    fun createMacro(project: Project, name: String): RpmMacroMacro {
        val file = createFile(project, name)
        return file.firstChild as RpmMacroMacro
    }

    private fun createFile(project: Project, text: String): RpmMacroFile =
        PsiFileFactory.getInstance(project).createFileFromText("dummy.macros", RpmSpecFileType, text) as RpmMacroFile
}
