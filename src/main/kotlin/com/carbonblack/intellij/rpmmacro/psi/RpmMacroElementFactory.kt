package com.carbonblack.intellij.rpmmacro.psi

import com.carbonblack.intellij.rpmspec.RpmSpecFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.*

object RpmMacroElementFactory {
    fun createMacro(project: Project, name: String): RpmMacroMacro {
        val file = createFile(project, name)
        return file.firstChild as RpmMacroMacro
    }

    private fun createFile(project: Project, text: String): RpmMacroFile {
        val name = "dummy.macros"
        return PsiFileFactory.getInstance(project).createFileFromText(name, RpmSpecFileType, text) as RpmMacroFile
    }
}
