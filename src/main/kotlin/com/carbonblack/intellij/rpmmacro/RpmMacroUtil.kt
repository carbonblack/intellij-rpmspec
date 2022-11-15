package com.carbonblack.intellij.rpmmacro

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroFile
import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmspec.RpmSpecSettingsState
import com.intellij.openapi.diagnostic.Logger
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
import kotlinx.coroutines.*
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.streams.asSequence

private val log = Logger.getInstance(RpmMacroUtil::class.java)

private fun List<String>.runCommand(workingDir: File? = null, action: (String) -> Unit) {
    val proc = ProcessBuilder()
        .command(this)
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()

    runBlocking {
        val child = launch(Dispatchers.IO) {
            if (!proc.waitFor(5, TimeUnit.SECONDS)) {
                proc.destroy()
                cancel()
            }
        }

        proc.inputStream.bufferedReader().forEachLine {
            action(it)
        }

        child.join()
        if (child.isCancelled) {
            cancel()
        }

        check(proc.exitValue() == 0) { "RPM process exited with non-zero return code: ${proc.exitValue()}" }
    }
}

object RpmMacroUtil {
    val macroPathFiles: Set<VirtualFile> by lazy {
        val paths = try {
            getRpmMacroPaths()
        } catch (e: Exception) {
            log.warn("Error finding system macro paths.", e)
            emptyList()
        }

        // Find files matching macro paths
        paths.flatMap { path ->
            if (path.contains("*")) {
                val start = path.substringBeforeLast("/")
                val glob = path.substringAfterLast("/")

                val startPath = Paths.get(start)
                val matcher = FileSystems.getDefault().getPathMatcher("glob:$glob")

                if (startPath.exists() && startPath.isDirectory()) {
                    Files.walk(startPath).asSequence()
                        .filter { it?.isFile() == true && matcher.matches(it.fileName) }
                        .mapNotNull { LocalFileSystem.getInstance().findFileByPath(it.toString()) }
                        .toList()
                } else emptyList()
            } else {
                LocalFileSystem.getInstance().findFileByPath(path)?.let { listOf(it) } ?: emptyList()
            }
        }.toSet()
    }

    @TestOnly
    fun getRpmMacroPaths(): List<String> {
        // Parse the rpm macro paths
        val paths = mutableListOf<String>()
        var target: String? = null

        val pathsRegex = Regex("^Macro path: (.*)$")
        val targetRegex = Regex("^\\S+:\\s+_target\\s+(.*)$")
        listOf(RpmSpecSettingsState.instance.rpmCommandPath, "--showrc").runCommand { line ->
            if (paths.isEmpty()) {
                pathsRegex.find(line)?.groups?.get(1)?.let { match ->
                    paths += match.value.split(":")
                }
            }
            if (target == null) {
                targetRegex.find(line)?.groups?.get(1)?.let { match ->
                    target = match.value
                }
            }
        }

        return paths.map {
            it.replace("%{_target}", target ?: "x86_64-linux")
        }
    }

    fun findMacros(project: Project, key: String): List<RpmMacroMacro> {
        val virtualFiles = FileTypeIndex.getFiles(RpmMacroFileType, GlobalSearchScope.everythingScope(project))
        val rpmMacroFiles = virtualFiles.map {
            PsiManager.getInstance(project).findFile(it)
        }.filterIsInstance<RpmMacroFile>()

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
