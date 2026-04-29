/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.themis.gui.base;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Base Document Builder for source.
 */
public abstract class ThemisUIBaseDocument
        implements ThemisUIDocBuilder {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(ThemisUIBaseDocument.class);

    /**
     * The Table standard class.
     */
    public static final String CLASSTBLSTD = "tableStd";

    /**
     * The Table Zebra class.
     */
    public static final String CLASSTBLZEBRA = "tableZebra";

    /**
     * The document builder.
     */
    private final DocumentBuilder theBuilder;

    /**
     * The Transformer.
     */
    private final Transformer theXformer;

    /**
     * The document.
     */
    private Document theDocument;

    /**
     * Constructor.
     *
     * @throws OceanusException on error
     */
    protected ThemisUIBaseDocument() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the document builder */
            final DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            myDocFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myDocFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myDocFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            theBuilder = myDocFactory.newDocumentBuilder();

            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            theXformer = myXformFactory.newTransformer();
            theXformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        } catch (Exception e) {
            throw new ThemisIOException("Failed to create", e);
        }
    }

    /**
     * Obtain the document.
     *
     * @return the document
     */
    public Document getDocument() {
        return theDocument;
    }

    /**
     * Create new document.
     *
     * @return the body
     */
    protected Element newDocument() {
        /* Create the new document */
        theDocument = theBuilder.newDocument();

        /* Create the standard structure */
        final Element myHtml = createElement(ThemisUIHTMLTag.HTML);
        theDocument.appendChild(myHtml);
        final Element myBody = createElement(ThemisUIHTMLTag.BODY);
        myHtml.appendChild(myBody);
        return myBody;
    }

    @Override
    public Element createElement(final ThemisUIHTMLTag pTag) {
        return theDocument.createElement(pTag.getTag());
    }

    @Override
    public Text createTextNode(final String pText) {
        return theDocument.createTextNode(pText);
    }

    @Override
    public Text createTextNode(final char pChar) {
        return createTextNode("" + pChar);
    }

    @Override
    public void setAttribute(final Element pElement,
                             final ThemisUIHTMLAttr pAttr,
                             final String pValue) {
        pElement.setAttribute(pAttr.getAttr(), pValue);
    }

    @Override
    public void addClassToElement(final Element pElement,
                                  final String pClass) {
        final String myAttr = ThemisUIHTMLAttr.CLASS.getAttr();
        final String myExist = pElement.getAttribute(myAttr);
        if (myExist.isEmpty()) {
            pElement.setAttribute(myAttr, pClass);
        } else {
            pElement.setAttribute(myAttr, myExist + ThemisChar.BLANK + pClass);
        }
    }

    @Override
    public void addToolTipToElement(final Element pElement,
                                    final String pToolTip) {
        addClassToElement(pElement, "toolTipHolder");
        final Element myToolTip = createElement(ThemisUIHTMLTag.SPAN);
        pElement.appendChild(myToolTip);
        addClassToElement(myToolTip, "toolTip");
        myToolTip.setTextContent(pToolTip);
    }

    /**
     * Format XML.
     *
     * @return the formatted XML
     */
    protected String formatXML() {
        /* Protect against exceptions */
        try {
            /* Transform the new document */
            final StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));
            return myWriter.getBuffer().toString();

        } catch (TransformerException e) {
            LOGGER.error("Failed to format", e);
            return "";
        }
    }
}
