/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * 把 {@code <script>} 字符串解析成 {@link XNode} 树，结果等价于 XPathParser 的 {@code evalNode("/script")}。
 * <p>
 * 与 XPathParser 的差别只在性能上：它每解析一条脚本都要新建 JAXP 工厂、{@link DocumentBuilder} 和 XPath 引擎，再对根节点做一次 XPath 求值。启动注入时
 * createSqlSource 会被成百上千次调用，这些重复初始化占了可观的启动时间。这里按线程缓存 {@link DocumentBuilder} 并直接取文档根节点，省掉 XPath 引擎，解析设置与
 * XPathParser#createDocument 保持一致。
 * </p>
 *
 * @author Drifter-yh
 * @since 3.5.18
 */
final class MybatisScriptParser {

    private static final EntityResolver ENTITY_RESOLVER = new XMLMapperEntityResolver();

    private static final ErrorHandler ERROR_HANDLER = new ErrorHandler() {

        @Override
        public void warning(SAXParseException exception) {
            // NOP 与 XPathParser 保持一致
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    };

    /**
     * {@link DocumentBuilder} 不是线程安全的，按线程各缓存一个；每次解析前 {@link DocumentBuilder#reset()} 恢复初始状态，避免上一次解析的残留在失败后影响下一次。
     */
    private static final ThreadLocal<DocumentBuilder> DOCUMENT_BUILDER = ThreadLocal
            .withInitial(MybatisScriptParser::newDocumentBuilder);

    private MybatisScriptParser() {
    }

    static XNode parse(String script, Properties variables) {
        DocumentBuilder builder = DOCUMENT_BUILDER.get();
        builder.reset();
        builder.setEntityResolver(ENTITY_RESOLVER);
        builder.setErrorHandler(ERROR_HANDLER);
        try {
            Document document = builder.parse(new InputSource(new StringReader(script)));
            /*
             * 等价于 XPathParser#evalNode("/script")：调用方已保证脚本以 <script> 开头，根元素就是 /script。
             * 这棵树只会被 MybatisXMLScriptBuilder 通过节点/属性访问方法读取，不会触发 XPath 求值。
             */
            return new XNode(null, document.getDocumentElement(), variables);
        } catch (SAXException | IOException e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }

    private static DocumentBuilder newDocumentBuilder() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(false);
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }
}
