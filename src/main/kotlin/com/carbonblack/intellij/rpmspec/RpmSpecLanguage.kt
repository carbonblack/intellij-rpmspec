package com.carbonblack.intellij.rpmspec

import com.intellij.lang.Language

class RpmSpecLanguage private constructor() : Language("RpmSpec") {
    companion object {
        val INSTANCE = RpmSpecLanguage()
    }
}
