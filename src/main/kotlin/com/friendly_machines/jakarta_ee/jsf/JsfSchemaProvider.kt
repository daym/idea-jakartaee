package com.friendly_machines.jakarta_ee.jsf

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.xml.XmlFile
import com.intellij.xml.XmlSchemaProvider
import com.jetbrains.rd.util.string.print
import io.ktor.utils.io.*
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class JsfSchemaProvider : XmlSchemaProvider() {
    private val SCHEMAS_BUNDLE_KEY: Key<CachedValue<Map<String, XmlFile>>> = Key.create("jsf_schemas")
    override fun isAvailable(file: XmlFile): Boolean {
        if ((file.name.endsWith(".xhtml") || file.name.endsWith(".jsf")) && file.rootTag?.getAttributeValue("xmlns") == "http://www.w3.org/1999/xhtml") {
            // .composition|.decorate|.include: http://java.sun.com/jsf/facelets jakarta.faces.facelets
            // tr.: jakarta.faces.html
            // TODO file.document.getRootTagNSDescriptor().
            // TODO find "{jakarta.faces.html}head" or "{jakarta.faces.html}body" or "{http://java.sun.com/jsf/facelets}composition"
            return true
        }
        return false
    }

    data class TaglibNamespace(val ns: String, val text: String)

    /** Given a taglib, extract the value in the <namespace> element and return it */
    private fun extractTaglibNamespace(taglibText: String): TaglibNamespace? {
        val factory = DocumentBuilderFactory.newNSInstance()
        val builder = factory.newDocumentBuilder()
        val source = InputSource(StringReader(taglibText))
        val root = builder.parse(source).documentElement
        if (root.tagName == "facelet-taglib") { // TODO check xmlns ?
            for (namespaceOfNamespace in arrayOf("https://jakarta.ee/xml/ns/jakartaee", "http://xmlns.jcp.org/xml/ns/javaee", "http://java.sun.com/xml/ns/javaee")) {
                var namespaces = root.getElementsByTagNameNS(namespaceOfNamespace, "namespace")
                if (namespaces.length != 0) {
                    val namespace = namespaces.item(0) as Element
                    return TaglibNamespace(namespaceOfNamespace, namespace.textContent)
                }
            }
        }
        return null
    }

    private fun generateXsd(taglibText: String, taglibNamespace: TaglibNamespace): String {
        var namespace = taglibNamespace.text
        val transform = javaClass.getResourceAsStream("/TaglibToXSD.xslt")!!.readAllBytes().toString(Charsets.UTF_8).replace("\$tlibNamespace", taglibNamespace.ns)
        val transformer: Transformer =
            TransformerFactory.newInstance().newTransformer(StreamSource(StringReader(transform)))
        //val outFile: File = File(outDir, taglibFile.getName().replaceFirst("\\.taglib\\.xml", ".xsd"))
        val result = StringWriter()
        //transformer.setParameter("tlibNamespace", taglibNamespace.ns)
        transformer.transform(StreamSource(StringReader(taglibText)), StreamResult(result))
        return result.toString()
    }

    private fun parseTaglib(
        project: Project,
        taglib: VirtualFile,
        destination: MutableMap<String, XmlFile>
    ) {
        val taglibText = String(taglib.contentsToByteArray())
        val taglibNamespace = extractTaglibNamespace(taglibText) ?: return
        val xsdText = generateXsd(taglibText, taglibNamespace)
        // Note: This file is in-memory. If you want to save it to disk, use PsiDirectory.add().
        val xsd: XmlFile =
            PsiFileFactory.getInstance(project).createFileFromText(
                String.format("taglib-%s.xsd", FileUtil.sanitizeFileName(taglibNamespace.text)),
                XmlFileType.INSTANCE, // TODO FileTypes.XsdFile
                xsdText,
                taglib.modificationStamp,
                true,
                true/*generated*/
            ) as XmlFile
        // Remember what the original file was.
        // Crashes
//        taglib.getPsiFile(project).let {
//            xsd.putUserData<PsiFile>(PsiFileFactory.ORIGINAL_FILE, it)
//        }
        destination[taglibNamespace.text] = xsd
    }

    private fun computeSchemas(module: Module): CachedValueProvider.Result<Map<String, XmlFile>> {
        val dependencies = ArrayList<Any>()
        // TODO single out jsf implementation (for example mojarra) and extension libs (for example PrimeFaces) only
        dependencies.add(ProjectRootManager.getInstance(module.project))
        val schemas: MutableMap<String, XmlFile> = HashMap()
        val taglibFiles = FilenameIndex.getAllFilesByExt(
            module.project,
            "taglib.xml",
            GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
        )
        for (taglibFile in taglibFiles) {
            try {
                parseTaglib(module.project, taglibFile, schemas)
            } catch (e: ProcessCanceledException) {
                throw e // do not cache
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
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