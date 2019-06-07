package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroFile
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacroDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.*

object RpmMacroUtil {
    fun findMacros(project: Project, key: String): List<RpmMacroMacro> {
        var result = emptyList<RpmMacroMacro>()
        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.allScope(project))
        val RpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(project).findFile(it) }.filter { it is RpmMacroFile }
        for (file in RpmMacroFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java)
            result = macros.filter { it.name == key }
        }
        return result
    }

    fun findMacros(file: PsiFile, key: String): List<RpmMacroMacro> {
        var result = emptyList<RpmMacroMacro>()
        if (file is RpmMacroFile) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java)
            result = macros.filter { it.name == key }
        }
        return result
    }

    fun findMacroDefinitions(file: PsiFile, key: String): List<RpmMacroMacroDefinition> {
        var result = emptyList<RpmMacroMacroDefinition>()
        if (file is RpmMacroFile) {
            val definitions = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacroDefinition::class.java)
            result = definitions.filter { it.macro.name == key }
        }
        return result
    }


    fun findMacroDefinitions(project: Project, key: String): List<RpmMacroMacroDefinition> {
        var result = emptyList<RpmMacroMacroDefinition>()
        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.allScope(project))
        val RpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(project).findFile(it) }.filter { it is RpmMacroFile }
        for (file in RpmMacroFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacroDefinition::class.java)
            result = macros.filter { it.macro.name == key }
        }
        return result
    }

    fun findMacros(project: Project): List<RpmMacroMacro> {
        val result = ArrayList<RpmMacroMacro>()
        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.allScope(project))
        val RpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(project).findFile(it) }.filter { it is RpmMacroFile }
        for (file in RpmMacroFiles) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java)
            result.addAll(macros)
        }
        return result
    }

    fun findMacros(file: PsiFile): List<RpmMacroMacro> {
        val result = ArrayList<RpmMacroMacro>()
        if (file is RpmMacroFile) {
            val macros = PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java)
            result.addAll(macros)
        }
        return result
    }
}
