package com.carbonblack.intellij.rpmspec.psi

import com.carbonblack.intellij.rpmspec.RpmSpecFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.*

object RpmSpecElementFactory {
    fun createMacro(project: Project, name: String): RpmSpecMacro {
        return createFile(project, name).firstChild as RpmSpecMacro
    }

    fun createTag(project: Project, name: String): RpmSpecTag {
        return createFile(project, name).firstChild as RpmSpecTag
    }

    private fun createFile(project: Project, text: String): RpmSpecFile {
        return PsiFileFactory.getInstance(project).createFileFromText("dummy.spec", RpmSpecFileType, text) as RpmSpecFile
    }
}
