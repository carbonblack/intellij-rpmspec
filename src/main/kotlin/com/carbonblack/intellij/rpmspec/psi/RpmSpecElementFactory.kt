package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.*

object RpmSpecElementFactory {
    fun createMacro(project: Project, name: String): RpmSpecMacro {
        val file = createFile(project, name)
        return file.firstChild as RpmSpecMacro
    }

    fun createTag(project: Project, name: String): RpmSpecTag {
        val file = createFile(project, name)
        return file.firstChild as RpmSpecTag
    }

    private fun createFile(project: Project, text: String): RpmSpecFile {
        val name = "dummy.spec"
        return PsiFileFactory.getInstance(project).createFileFromText(name, RpmSpecFileType, text) as RpmSpecFile
    }
}
