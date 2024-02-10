package com.friendly_machines.jakarta_ee.faces_config

import com.intellij.lang.xhtml.XHTMLParserDefinition
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile

class FacesConfigParserDefinition : XHTMLParserDefinition() {
        override fun createFile(viewProvider: FileViewProvider): PsiFile {
            // XmlFileImpl(viewProvider, XmlElementType.XML_FILE)
            return FacesConfigFile(viewProvider)
        }
}
