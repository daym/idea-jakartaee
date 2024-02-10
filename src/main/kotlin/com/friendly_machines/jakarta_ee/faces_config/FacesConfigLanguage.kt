package com.friendly_machines.jakarta_ee.faces_config

import com.intellij.lang.xhtml.XHTMLLanguage
import com.intellij.lang.xml.XMLLanguage

class FacesConfigLanguage: XMLLanguage(XMLLanguage.INSTANCE, "FacesConfig", "text/faces-config+xml") {
    companion object {
        val INSTANCE: FacesConfigLanguage by lazy {
            FacesConfigLanguage()
        }
    }
}