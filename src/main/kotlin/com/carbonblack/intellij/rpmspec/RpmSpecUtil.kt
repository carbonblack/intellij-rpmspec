package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmspec.psi.RpmSpecFile
import com.carbonblack.intellij.rpmspec.psi.RpmSpecPreamble
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.*
import com.intellij.psi.util.PsiTreeUtil

import java.util.*

object RpmSpecUtil {
    fun findProperties(project: Project, key: String): List<RpmSpecPreamble> {
        var result: MutableList<RpmSpecPreamble>? = null
        val virtualFiles = FileTypeIndex.getFiles(RpmSpecFileType.INSTANCE, GlobalSearchScope.allScope(project))
        for (virtualFile in virtualFiles) {
            val simpleFile = PsiManager.getInstance(project).findFile(virtualFile) as RpmSpecFile?
            if (simpleFile != null) {
                val properties = PsiTreeUtil.getChildrenOfType(simpleFile, RpmSpecPreamble::class.java)
                if (properties != null) {
                    for (property in properties) {
                        if (key == property.key) {
                            if (result == null) {
                                result = ArrayList()
                            }
                            result.add(property)
                        }
                    }
                }
            }
        }
        return result ?: emptyList()
    }

    fun findProperties(project: Project): List<RpmSpecPreamble> {
        val result = ArrayList<RpmSpecPreamble>()
        val virtualFiles = FileTypeIndex.getFiles(RpmSpecFileType.INSTANCE, GlobalSearchScope.allScope(project))
        for (virtualFile in virtualFiles) {
            val simpleFile = PsiManager.getInstance(project).findFile(virtualFile) as RpmSpecFile?
            if (simpleFile != null) {
                val properties = PsiTreeUtil.getChildrenOfType(simpleFile, RpmSpecPreamble::class.java)
                if (properties != null) {
                    result.addAll(properties)
                }
            }
        }
        return result
    }
}
