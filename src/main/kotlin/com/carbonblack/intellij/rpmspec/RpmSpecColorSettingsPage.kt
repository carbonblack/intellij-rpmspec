package com.carbonblack.intellij.rpmspec

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.*

private val DESCRIPTORS = arrayOf(
        AttributesDescriptor("Key", RpmSpecSyntaxHighligher.KEY),
        AttributesDescriptor("Separator", RpmSpecSyntaxHighligher.SEPARATOR),
        AttributesDescriptor("Value", RpmSpecSyntaxHighligher.VALUE),
        AttributesDescriptor("Body item", RpmSpecSyntaxHighligher.BODY_ITEM),
        AttributesDescriptor("Macro braces", RpmSpecSyntaxHighligher.MACRO_ITEM),
        AttributesDescriptor("Macro", RpmSpecSyntaxHighligher.MACRO_VALUE_ITEM),
        AttributesDescriptor("Comment", RpmSpecSyntaxHighligher.COMMENT),
        AttributesDescriptor("Text", RpmSpecSyntaxHighligher.TEXT),
        AttributesDescriptor("Changelog date", RpmSpecSyntaxHighligher.CHANGELOG_DATE),
        AttributesDescriptor("Changelog name", RpmSpecSyntaxHighligher.CHANGELOG_NAME),
        AttributesDescriptor("Changelog email", RpmSpecSyntaxHighligher.CHANGELOG_EMAIL),
        AttributesDescriptor("Version", RpmSpecSyntaxHighligher.VERSION))

class RpmSpecColorSettingsPage : ColorSettingsPage {

    override fun getIcon() = RpmSpecIcons.FILE

    override fun getHighlighter() = RpmSpecSyntaxHighligher()

    override fun getDemoText() = """
        Name:           cello
        Version:        1.0
        Release:        1%{?dist}
        Summary:        Hello World example implemented in C

        License:        GPLv3+
        URL:            https://www.example.com/%{name}
        Source0:        https://www.example.com/%{name}/releases/%{name}-%{version}.tar.gz

        Patch0:         cello-output-first-patch.patch

        BuildRequires:  gcc
        BuildRequires:  make

        # This  is a comment and should be greyed out
        %description
        The long-tail description for our Hello World Example implemented in
        C.
        %prep
        %setup -q

        %patch0

        %build
        make %{?_smp_mflags}

        %install
        %make_install

        %files
        %license LICENSE
        %{_bindir}/%{name}

        %changelog
        * Tue May 31 2016 Adam Miller <maxamillion@fedoraproject.org> - 1.0-1
        - First cello package""".trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors() = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = "RPM SPEC"
}
