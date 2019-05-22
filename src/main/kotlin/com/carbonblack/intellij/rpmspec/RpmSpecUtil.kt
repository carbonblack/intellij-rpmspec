package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecFile
import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.*

object RpmSpecUtil {
    fun findMacros(project: Project, key: String): List<RpmSpecMacro> {
        var result = emptyList<RpmSpecMacro>()
        val virtualFiles = FileTypeIndex.getFiles(RpmSpecFileType.INSTANCE, GlobalSearchScope.allScope(project))
        val rpmSpecFiles  = virtualFiles.map { PsiManager.getInstance(project).findFile(it) }.filter { it is RpmSpecFile }
        for (file in rpmSpecFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmSpecMacro::class.java)
            result = macros.filter { it.name == key }
        }
        return result
    }

    fun findMacros(file: PsiFile, key: String): List<RpmSpecMacro> {
        var result = emptyList<RpmSpecMacro>()
        if (file is RpmSpecFile) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmSpecMacro::class.java)
            result = macros.filter { it.name == key }
        }
        return result
    }

    fun findMacros(project: Project): List<RpmSpecMacro> {
        val result = ArrayList<RpmSpecMacro>()
        val virtualFiles = FileTypeIndex.getFiles(RpmSpecFileType.INSTANCE, GlobalSearchScope.allScope(project))
        val rpmSpecFiles  = virtualFiles.map { PsiManager.getInstance(project).findFile(it) }.filter { it is RpmSpecFile }
        for (file in rpmSpecFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmSpecMacro::class.java)
            result.addAll(macros)
        }
        return result
    }

    fun findMacros(file: PsiFile): List<RpmSpecMacro> {
        val result = ArrayList<RpmSpecMacro>()
        if (file is RpmSpecFile) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmSpecMacro::class.java)
            result.addAll(macros)
        }
        return result
    }
}
