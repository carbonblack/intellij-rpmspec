package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroFile
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.io.exists
import com.intellij.util.io.isDirectory
import com.intellij.util.io.isFile
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths

import java.util.concurrent.TimeUnit
import java.io.BufferedReader
import kotlin.streams.asSequence

object RpmMacroUtil {

    private fun String.runCommand(workingDir: File? = null): String {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

        return proc.inputStream.bufferedReader().use(BufferedReader::readText).also {
            proc.waitFor(5, TimeUnit.SECONDS)
        }
    }

    val macroPathFiles: Set<VirtualFile> by lazy {
        // Parse the rpm macro paths
        val paths = try {
            "rpm --showrc".runCommand().let runCommand@ { output ->
                val regex = Regex("Macro path: (.*)")
                for (line in output.split("\n")) {
                    regex.find(line)?.groups?.get(1)?.let { match ->
                        return@runCommand match.value.replace("%{_target}", "x86_64-linux").split(":")
                    }
                }
                null
            }
        } catch (e: Exception) { null } ?: emptyList()

        // Find files matching macro paths
        paths.flatMap { path ->
            if (path.contains("*")) {
                val start = path.substringBeforeLast("/")
                val glob = path.substringAfterLast("/")

                val startPath = Paths.get(start)
                val matcher = FileSystems.getDefault().getPathMatcher("glob:$glob")

                if (startPath.exists() && startPath.isDirectory()) {
                    Files.walk(startPath).asSequence()
                            .filter { it?.isFile() == true && matcher.matches(it.fileName)  }
                            .mapNotNull { LocalFileSystem.getInstance().findFileByPath(it.toString()) }
                            .toList()
                } else emptyList()
            } else {
                LocalFileSystem.getInstance().findFileByPath(path)?.let { listOf(it) } ?: emptyList()
            }
        }.toSet()
    }

    fun findMacros(project: Project, key: String): List<RpmMacroMacro> {
        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(project))
        val rpmMacroFiles  = virtualFiles.map { PsiManager.getInstance(project).findFile(it) }.filterIsInstance<RpmMacroFile>()

        return rpmMacroFiles.flatMap { file ->
            PsiTreeUtil.findChildrenOfType(file, RpmMacroMacro::class.java).filter { it.name == key }
        }
    }

    fun findMacros(file: PsiFile): Collection<RpmMacroMacro> {
        return (file as? RpmMacroFile)?.let { macroFile ->
            PsiTreeUtil.findChildrenOfType(macroFile, RpmMacroMacro::class.java)
        } ?: emptyList()
    }
}
