package com.friendly_machines.jakarta_ee.faces_config

import com.friendly_machines.jakarta_ee.JakartaeeIcons
import com.intellij.ide.highlighter.HtmlFileType
import javax.swing.Icon


class FacesConfigFileType : HtmlFileType(FacesConfigLanguage.INSTANCE) {
    companion object {
        val INSTANCE = FacesConfigFileType()
    }
    override fun getName(): String {
        return "FacesConfig"
    }

    override fun getDescription(): String {
        return "Java server faces config"
    }

    override fun getDefaultExtension(): String {
        return "xml"
    }

    override fun getIcon(): Icon {
        return JakartaeeIcons.JSF_FILE
    }
}