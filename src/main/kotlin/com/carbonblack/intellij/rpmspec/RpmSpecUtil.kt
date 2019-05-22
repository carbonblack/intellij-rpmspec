package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecFile
import com.carbonblack.intellij.rpmspec.psi.RpmSpecMacro
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.*

object RpmSpecUtil {
    fun findMacros(project: Project, key: String): List<RpmSpecMacro> {
        var result: MutableList<RpmSpecMacro>? = null
        val virtualFiles = FileTypeIndex.getFiles(RpmSpecFileType.INSTANCE, GlobalSearchScope.allScope(project))
        for (virtualFile in virtualFiles) {
            val rpmSpecFile = PsiManager.getInstance(project).findFile(virtualFile) as RpmSpecFile?
            if (rpmSpecFile != null) {
                val macros = PsiTreeUtil.findChildrenOfType(rpmSpecFile, RpmSpecMacro::class.java)
                for (macro in macros) {
                    if (key == macro.name) {
                        if (result == null) {
                            result = ArrayList()
                        }
                        result.add(macro)
                    }
                }
            }
        }
        return result ?: emptyList()
    }

    fun findMacros(project: Project): List<RpmSpecMacro> {
        val result = ArrayList<RpmSpecMacro>()
        val virtualFiles = FileTypeIndex.getFiles(RpmSpecFileType.INSTANCE, GlobalSearchScope.allScope(project))
        for (virtualFile in virtualFiles) {
            val rpmSpecFile = PsiManager.getInstance(project).findFile(virtualFile) as RpmSpecFile?
            if (rpmSpecFile != null) {
                val macros = PsiTreeUtil.findChildrenOfType(rpmSpecFile, RpmSpecMacro::class.java)
                result.addAll(macros)
            }
        }
        return result
    }
}
