package com.lkl.android.lint.plugin.utils

import com.lkl.android.lint.plugin.utils.IOUtils.closeQuietly
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Result
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

/**
 * xml文件合并工具类
 *
 * @author lkl
 * @since 2022/02/28
 */
object XMLMergeUtils {
    @Throws(Exception::class)
    fun merge(
        outputStream: OutputStream, expression: String?, vararg inputStreams: InputStream?
    ) {
        val xPathFactory = XPathFactory.newInstance()
        val xpath = xPathFactory.newXPath()
        val compiledExpression = xpath.compile(expression)
        val doc: Document = mergeXml(compiledExpression, *inputStreams)
        print(doc, outputStream)
        closeQuietly(*inputStreams)
        closeQuietly(outputStream)
    }

    @Throws(Exception::class)
    fun merge(expression: String?, vararg inputStreams: InputStream?): Document {
        val xPathFactory = XPathFactory.newInstance()
        val xpath = xPathFactory.newXPath()
        val compiledExpression = xpath.compile(expression)
        return mergeXml(compiledExpression, *inputStreams)
    }

    @Throws(Exception::class)
    private fun mergeXml(
        expression: XPathExpression, vararg inputStreams: InputStream?
    ): Document {
        val docBuilderFactory = DocumentBuilderFactory.newInstance()
        docBuilderFactory.isIgnoringElementContentWhitespace = true
        val docBuilder = docBuilderFactory.newDocumentBuilder()
        val base = docBuilder.parse(inputStreams[0])
        val results = expression.evaluate(
            base, XPathConstants.NODE
        ) as Node ?: throw IOException(
            inputStreams[0].toString() + ": expression does not evaluate to node"
        )
        for (i in 1 until inputStreams.size) {
            if (inputStreams[i] == null) {
                continue
            }
            val merge = docBuilder.parse(inputStreams[i])
            val nextResults = expression.evaluate(merge, XPathConstants.NODE) as Node
            while (nextResults.hasChildNodes()) {
                var kid = nextResults.firstChild
                nextResults.removeChild(kid)
                kid = base.importNode(kid, true)
                results.appendChild(kid)
            }
        }
        return base
    }

    @Throws(Exception::class)
    private fun print(doc: Document, targetFile: OutputStream) {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(doc)
        val result: Result = StreamResult(targetFile)
        transformer.transform(source, result)
    }
}