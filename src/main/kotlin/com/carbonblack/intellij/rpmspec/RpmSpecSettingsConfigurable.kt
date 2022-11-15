package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmmacro.RpmMacroUtil
import com.intellij.openapi.options.Configurable
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.Insets
import javax.swing.*
import kotlin.reflect.KProperty

private class TextFieldDelegate(private val textField: JBTextField) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return textField.text
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        textField.text = value
    }
}

class RpmSpecSettingsConfigurable : Configurable {
    override fun getDisplayName() = "RPM SPEC File"
    override fun getPreferredFocusedComponent(): JComponent = rpmCommandPathBox

    private val rpmCommandPathBox = JBTextField("rpm")
    private var rpmCommandPath: String by TextFieldDelegate(rpmCommandPathBox)

    private val detectedPathsList = JBList<String>().apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        border = IdeBorderFactory.createEmptyBorder(Insets(0, 3, 0, 0))
        isFocusable = false
    }

    override fun createComponent(): JComponent {
        val detectedMacrosPanel = JPanel(BorderLayout()).apply {
            // This is for if we decide to make the list of macros editable
            /* val toolbarDecorator = ToolbarDecorator.createDecorator(detectedPathsList)
                .setScrollPaneBorder(JBUI.Borders.empty())
                .setPanelBorder(JBUI.Borders.customLine(JBColor.border(), 0, 1, 0, 1))
            add(toolbarDecorator.createPanel(), BorderLayout.NORTH) */
            add(JBScrollPane(detectedPathsList), BorderLayout.CENTER)
            border = IdeBorderFactory.createTitledBorder("Detected Macro Files:", false).setShowLine(false)
        }

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Path to rpm executable:", rpmCommandPathBox)
            .addComponentFillVertically(detectedMacrosPanel, 12)
            .panel
    }

    override fun isModified(): Boolean {
        return rpmCommandPath != RpmSpecSettingsState.instance.rpmCommandPath
    }

    override fun apply() {
        RpmSpecSettingsState.instance.rpmCommandPath = rpmCommandPath
    }

    override fun reset() {
        rpmCommandPath = RpmSpecSettingsState.instance.rpmCommandPath
        detectedPathsList.setListData(RpmMacroUtil.macroPathFiles.mapNotNull { it.canonicalPath }.toTypedArray())
    }
}
