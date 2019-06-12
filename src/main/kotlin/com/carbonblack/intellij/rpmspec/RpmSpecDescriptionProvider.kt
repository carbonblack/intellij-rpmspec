package com.carbonblack.intellij.rpmspec

import com.carbonblack.intellij.rpmmacro.psi.RpmMacroMacro
import com.carbonblack.intellij.rpmspec.psi.RpmSpecTag
import com.intellij.codeInsight.highlighting.HighlightUsagesDescriptionLocation
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.ElementDescriptionProvider
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewShortNameLocation
import com.intellij.usageView.UsageViewTypeLocation

class RpmSpecDescriptionProvider : ElementDescriptionProvider {
    override fun getElementDescription(element: PsiElement, location: ElementDescriptionLocation): String? {
        if (element is RpmSpecTag) {
            return when (location) {
                is UsageViewNodeTextLocation -> element.text
                is UsageViewShortNameLocation -> element.text
                is UsageViewLongNameLocation -> element.text
                is UsageViewTypeLocation -> ""
                is HighlightUsagesDescriptionLocation -> element.text
                else -> null
            }
        } else if (element is RpmMacroMacro) {
            return when (location) {
                is UsageViewNodeTextLocation -> element.text
                is UsageViewShortNameLocation -> element.text
                is UsageViewLongNameLocation -> element.text
                is UsageViewTypeLocation -> ""
                is HighlightUsagesDescriptionLocation -> element.text
                else -> null
            }
        }

        return null
    }
}