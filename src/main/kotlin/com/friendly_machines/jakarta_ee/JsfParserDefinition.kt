package com.friendly_machines.jakarta_ee

import com.intellij.lang.xhtml.XHTMLParserDefinition
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile

//import com.intellij.lang.xml.XMLParserDefinition

class JsfParserDefinition : XHTMLParserDefinition() {
//        override fun getFileNodeType(): IFileElementType = XmlElementType.XML_FILE
//        override fun getWhitespaceTokens(): TokenSet = XmlTokenType.WHITESPACES
//        override fun getCommentTokens(): TokenSet = XmlTokenType.COMMENTS
//        override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
//        override fun createParser(project: Project): PsiParser = XmlParser()
    //         return LanguageParserDefinitions.INSTANCE.forLanguage(Language.findInstance(XMLLanguage.class)).createParser(project);
//        override fun createElement(node: ASTNode): PsiElement = (node.elementType as XmlStubBasedElementType<*, *>).createPsi(node)
        override fun createFile(viewProvider: FileViewProvider): PsiFile {
            // XmlFileImpl(viewProvider, XmlElementType.XML_FILE)
            return JsfFile(viewProvider)
        }
//        override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements = com.intellij.lang.xml.XMLParserDefinition.Companion.canStickTokensTogether(left, right)
//    final Lexer lexer = createLexer(left.getPsi().getProject());
//    return XMLParserDefinition.canStickTokensTogetherByLexerInXml(left, right, lexer, 0);

//        companion object {
//            fun canStickTokensTogether(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
//                return if (left.elementType !== XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN && right.elementType !== XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
//                    if (left.elementType === XmlTokenType.XML_ATTRIBUTE_VALUE_END_DELIMITER && right.elementType === XmlTokenType.XML_NAME) {
//                        ParserDefinition.SpaceRequirements.MUST
//                    } else if (left.elementType === XmlTokenType.XML_NAME && right.elementType === XmlTokenType.XML_NAME) {
//                        ParserDefinition.SpaceRequirements.MUST
//                    } else {
//                        if (left.elementType === XmlTokenType.XML_TAG_NAME && right.elementType === XmlTokenType.XML_NAME) ParserDefinition.SpaceRequirements.MUST else ParserDefinition.SpaceRequirements.MAY
//                    }
//                } else {
//                    ParserDefinition.SpaceRequirements.MUST_NOT
//                }
//            }
//        }
//public override fun createFile(viewProvider: FileViewProvider): PsiFile {
//    return XmlFileImpl(viewProvider, XmlElementType.JSF_FILE)
//}

}
