package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmmacro.RpmMacroUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.exists
import com.intellij.util.xmlb.XmlSerializerUtil
import java.io.File
import java.nio.file.Paths

private val log = Logger.getInstance(RpmMacroUtil::class.java)

@State(name = "RpmSpecSettings", storages = [Storage("rpmSpec.xml")])
class RpmSpecSettingsState : PersistentStateComponent<RpmSpecSettingsState> {
    var rpmCommandPath = findRpmPath() ?: "rpm"

    override fun getState(): RpmSpecSettingsState = this
    override fun loadState(state: RpmSpecSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: RpmSpecSettingsState
            get() = ApplicationManager.getApplication().getService(RpmSpecSettingsState::class.java)

        private fun findRpmPath(): String? {
            return try {
                System.getenv().getOrDefault("PATH", "")
                    .split(File.pathSeparator)
                    .map { Paths.get(it).resolve("rpm") }
                    .find { it.exists() }
                    ?.toAbsolutePath()
                    ?.toString()
            } catch (e: Exception) {
                log.warn("Error trying to find rpm executable.", e)
                null
            }
        }
    }
}
