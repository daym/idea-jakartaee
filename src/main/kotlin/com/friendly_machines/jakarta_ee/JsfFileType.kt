package com.friendly_machines.jakarta_ee

import com.intellij.ide.highlighter.HtmlFileType
import javax.swing.Icon


class JsfFileType : HtmlFileType(JsfLanguage.INSTANCE) {
    companion object {
        val INSTANCE = JsfFileType()
    }
    override fun getName(): String {
        return "Jsf"
    }

    override fun getDescription(): String {
        return "Java server faces"
    }

    override fun getDefaultExtension(): String {
        return "jsf"
    }

    override fun getIcon(): Icon {
        return JakartaeeIcons.JSF_FILE
    }
}