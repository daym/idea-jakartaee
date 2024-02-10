package com.friendly_machines.jakarta_ee.faces_config

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.xml.XmlFile
import com.intellij.xml.XmlSchemaProvider

class FacesConfigSchemaProvider : XmlSchemaProvider() {
    companion object {
        const val NAMESPACE = "https://jakarta.ee/xml/ns/jakartaee"
    }
    private val SCHEMAS_BUNDLE_KEY: Key<CachedValue<Map<String, XmlFile>>> = Key.create("faces_config_schemas")
    override fun isAvailable(file: XmlFile): Boolean {
        return (file.name == "faces-config.xml" && file.rootTag?.namespace?.equals(NAMESPACE) == true)
    }

    private fun computeSchemas(module: Module): CachedValueProvider.Result<Map<String, XmlFile>> {
        val dependencies = ArrayList<Any>()
        // TODO single out jsf implementation only
        dependencies.add(ProjectRootManager.getInstance(module.project))
        val schemas: MutableMap<String, XmlFile> = HashMap()
        val xsdVirtualFiles = FilenameIndex.getAllFilesByExt(
            module.project,
            "web-facesconfig_4_0.xsd",
            GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
        )
        val project = module.project
        for (xsdVirtualFile in xsdVirtualFiles) {
            var xmlFile = PsiManager.getInstance(project).findFile(xsdVirtualFile) as XmlFile?
            val xsdText = xsdVirtualFile.contentsToByteArray().toString()
            if (xmlFile == null) { // likely
                val factory = PsiFileFactory.getInstance(project)
                // Note: This file is in-memory. If you want to save it to disk, use PsiDirectory.add().
                xmlFile =
                    factory.createFileFromText(
                        "web-facesconfig_4_0.xsd",
                        XmlFileType.INSTANCE, // TODO FileTypes.XsdFile
                        xsdText,
                        xsdVirtualFile.modificationStamp,
                        true,
                        true/*generated*/
                    ) as XmlFile
            }
            schemas.put(NAMESPACE, xmlFile)
        }
        return CachedValueProvider.Result<Map<String, XmlFile>>(schemas, dependencies.toArray())
    }

    private fun getSchemas(module: Module): Map<String, XmlFile> {
        val project = module.project
        val manager = CachedValuesManager.getManager(project)
        val bundle = manager.getCachedValue<Map<String, XmlFile>>(
            module, SCHEMAS_BUNDLE_KEY,
            CachedValueProvider<Map<String, XmlFile>> {
                try {
                    return@CachedValueProvider computeSchemas(module)
                } catch (pce: ProcessCanceledException) {
                    throw pce
                } catch (e: java.lang.Exception) {
                    //e.printStackTrace();
                    return@CachedValueProvider null
                }
            }, false
        )
        return bundle ?: emptyMap()
    }

    override fun getSchema(namespace: String, module: Module?, file: PsiFile): XmlFile? {
        if (module == null) {
            return null
        }
        try {
            return getSchemas(module)[namespace]
        } catch (e: ProcessCanceledException) {
            //e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return null
    }
}