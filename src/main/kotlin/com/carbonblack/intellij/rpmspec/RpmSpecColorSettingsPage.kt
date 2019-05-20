package com.carbonblack.intellij.rpmspec

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.*

import javax.swing.*

class RpmSpecColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon? = RpmSpecIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = RpmSpecSyntaxHighligher()

    override fun getDemoText(): String =
        "# You are reading the \".properties\" entry.\n" +
        "! The exclamation mark can also mark text as comments.\n" +
        "website = http://en.wikipedia.org/\n" +
        "language = English\n" +
        "# The backslash below tells the application to continue reading\n" +
        "# the value onto the next line.\n" +
        "message = Welcome to \\\n" +
        "          Wikipedia!\n" +
        "# Add spaces to the key\n" +
        "key\\ with\\ spaces = This is the value that could be looked up with the key \"key with spaces\".\n" +
        "# Unicode\n" +
        "tab : \\u0009"

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "RPM SPEC"

    companion object {
        private val DESCRIPTORS = arrayOf(
                AttributesDescriptor("Key", RpmSpecSyntaxHighligher.KEY),
                AttributesDescriptor("Separator", RpmSpecSyntaxHighligher.SEPARATOR),
                AttributesDescriptor("Value", RpmSpecSyntaxHighligher.VALUE))
    }
}
