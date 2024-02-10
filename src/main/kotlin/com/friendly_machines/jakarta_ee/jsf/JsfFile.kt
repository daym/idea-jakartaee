package com.friendly_machines.jakarta_ee.jsf

import com.intellij.psi.FileViewProvider
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.xml.XmlFile

class JsfFile(viewProvider: FileViewProvider) : XmlFileImpl(viewProvider, JSF_FILE_ELEMENT_TYPE), XmlFile {
    companion object {
        val JSF_FILE_ELEMENT_TYPE: IFileElementType = IFileElementType("JSF_FILE_ELEMENT_TYPE", JsfLanguage.INSTANCE)
    }

    override fun toString(): String {
        return "JsfFile:" + getName()
    }
}