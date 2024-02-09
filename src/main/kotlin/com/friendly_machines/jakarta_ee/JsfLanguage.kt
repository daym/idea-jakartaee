package com.friendly_machines.jakarta_ee

import com.intellij.lang.Language
import com.intellij.lang.xhtml.XHTMLLanguage
import com.intellij.lang.xml.XMLLanguage

class JsfLanguage: XMLLanguage(XHTMLLanguage.INSTANCE, "Jsf", "text/xhtml+jsf") {
    companion object {
        val INSTANCE: JsfLanguage by lazy {
            JsfLanguage()
        }
    }
}