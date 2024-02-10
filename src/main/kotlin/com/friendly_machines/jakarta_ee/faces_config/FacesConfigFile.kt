package com.friendly_machines.jakarta_ee.faces_config

import com.intellij.psi.FileViewProvider
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.xml.XmlFile

class FacesConfigFile(viewProvider: FileViewProvider) : XmlFileImpl(viewProvider, FACES_CONFIG_FILE_ELEMENT_TYPE), XmlFile {
    companion object {
        val FACES_CONFIG_FILE_ELEMENT_TYPE: IFileElementType = IFileElementType("JSF_FILE_ELEMENT_TYPE", FacesConfigLanguage.INSTANCE)
    }

    override fun toString(): String {
        return "FacesConfigFile:" + getName()
    }
}