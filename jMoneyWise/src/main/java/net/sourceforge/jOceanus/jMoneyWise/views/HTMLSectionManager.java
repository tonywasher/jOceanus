/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Provides functionality to hide and restore sections of an HTML document. This is useful for displaying HTML documents in a jEditorPane, allowing a click to
 * open/close sections of the document.
 */
public class HTMLSectionManager {
    /**
     * The division element.
     */
    private static final String ELEMENT_DIV = "div";

    /**
     * The id attribute.
     */
    private static final String ATTR_ID = "id";

    /**
     * The class attribute.
     */
    private static final String ATTR_CLASS = "class";

    /**
     * The Document Builder.
     */
    private DocumentBuilder theBuilder;

    /**
     * The Transformer.
     */
    private Transformer theXformer;

    /**
     * The Current document.
     */
    private Document theDocument = null;

    /**
     * The Current text.
     */
    private String theText = null;

    /**
     * The element map.
     */
    private final HashMap<String, Element> theMap;

    /**
     * Constructor.
     */
    public HTMLSectionManager() {
        /* Allocate the hashMap */
        theMap = new HashMap<String, Element>();

        /* Protect against exceptions */
        try {
            /* Create the document builder */
            DocumentBuilderFactory myDocFactory = DocumentBuilderFactory.newInstance();
            theBuilder = myDocFactory.newDocumentBuilder();

            /* Create the transformer */
            TransformerFactory myXformFactory = TransformerFactory.newInstance();
            theXformer = myXformFactory.newTransformer();
            theXformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        } catch (Exception e) {
            theBuilder = null;
            theXformer = null;
        }
    }

    /**
     * Set Text.
     * @param pText the text to parse
     */
    public void setInitialText(final String pText) {
        /* Protect against exceptions */
        try {
            /* Store the text */
            theText = pText;

            /* Check for valid initialisation */
            if (theBuilder != null) {
                /* Access the XML document element */
                theMap.clear();
                theDocument = theBuilder.parse(new InputSource(new StringReader(pText)));
            }
        } catch (Exception e) {
            theDocument = null;
        }
    }

    /**
     * Toggle section.
     * @param pId the id of the text to toggle.
     * @return the modified text
     */
    public String toggleSection(final String pId) {
        /* If the section is hidden */
        if (theMap.get(pId) != null) {
            return restoreSection(pId);
        }

        /* Hide the section */
        return hideSection(pId);
    }

    /**
     * Hide all section of specified class.
     * @param pClass the class of the sections to hide.
     * @return the modified text
     */
    public String hideClassSections(final String pClass) {
        /* Ignore if we have no document or transformer */
        if ((theDocument == null) || (theXformer == null)) {
            /* Return current text */
            return theText;
        }

        /* Access root element */
        Element myElement = theDocument.getDocumentElement();

        /* Loop through the nodes */
        Node myNode = myElement.getFirstChild();
        while (myNode != null) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                myNode = myNode.getNextSibling();
                continue;
            }

            /* Access node as element and reposition to next sibling */
            Element myChild = (Element) myNode;
            myNode = myNode.getNextSibling();

            /* hide any class sections */
            hideClassSections(myChild, pClass);
        }

        /* Return the new text */
        return formatXML();
    }

    /**
     * Hide any sections of this class for the element and children.
     * @param pElement the element to search
     * @param pClass the class
     */
    private void hideClassSections(final Element pElement,
                                   final String pClass) {
        /* Loop through the child nodes */
        Node myNode = pElement.getFirstChild();
        while (myNode != null) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                myNode = myNode.getNextSibling();
                continue;
            }

            /* Access node as element and reposition to next sibling */
            Element myChild = (Element) myNode;
            myNode = myNode.getNextSibling();

            /* hide any children first */
            hideClassSections(myChild, pClass);
        }

        /* Check the element for the id */
        if (pElement.getAttribute(ATTR_CLASS).equals(pClass)) {
            /* Access the id and skip if there is no id */
            String myId = pElement.getAttribute(ATTR_ID);
            if (myId.length() == 0) {
                return;
            }
            /* Create a new placeholder section */
            Element myReplacement = theDocument.createElement(ELEMENT_DIV);
            myReplacement.setAttribute(ATTR_ID, myId);

            /* Access parent node */
            Node myParent = pElement.getParentNode();

            /* Remove the child and replace with dummy */
            myParent.replaceChild(myReplacement, pElement);

            /* Put the old element into the map */
            theMap.put(myId, pElement);
        }
    }

    /**
     * Hide section.
     * @param pId the id of the section to hide.
     * @return the modified text
     */
    public String hideSection(final String pId) {
        /* Ignore if we have no document or transformer */
        if ((theDocument == null) || (theXformer == null)) {
            /* Return current text */
            return theText;
        }

        /* Ignore if section is already hidden */
        if (theMap.get(pId) != null) {
            return theText;
        }

        /* Locate the section */
        Element mySection = getElementById(pId);
        if (mySection != null) {
            /* Access parent node */
            Node myParent = mySection.getParentNode();

            /* Create a new placeholder section */
            Element myReplacement = theDocument.createElement(ELEMENT_DIV);
            myReplacement.setAttribute(ATTR_ID, pId);

            /* Remove the child and replace with dummy */
            myParent.replaceChild(myReplacement, mySection);

            /* Put the old element into the map */
            theMap.put(pId, mySection);

            /* Return the new text */
            return formatXML();
        }

        /* Return the current text */
        return theText;
    }

    /**
     * Obtain element with the given id attribute.
     * @param pId the id
     * @return the relevant element (or null)
     */
    private Element getElementById(final String pId) {
        /* Access root element */
        Element myElement = theDocument.getDocumentElement();

        /* Loop through the nodes */
        for (Node myNode = myElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            Element myChild = (Element) myNode;
            Element myResult = checkElementForId(myChild, pId);
            if (myResult != null) {
                return myResult;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Obtain element with the given id attribute.
     * @param pElement the element to search
     * @param pId the id
     * @return the element or null if not found
     */
    private Element checkElementForId(final Element pElement,
                                      final String pId) {
        /* Check the element for the id */
        if (pElement.getAttribute(ATTR_ID).equals(pId)) {
            return pElement;
        }

        /* Loop through the child nodes */
        for (Node myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            Element myChild = (Element) myNode;

            /* Pass check on to child */
            Element myResult = checkElementForId(myChild, pId);
            if (myResult != null) {
                return myResult;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Restore section.
     * @param pId the id of the section to restore.
     * @return the modified text
     */
    public String restoreSection(final String pId) {
        /* Ignore if we have no document or transformer */
        if ((theDocument == null) || (theXformer == null)) {
            /* Return current text */
            return theText;
        }

        /* Obtain the hidden element */
        Element myHidden = theMap.get(pId);

        /* If we have hidden an element */
        if (myHidden != null) {
            /* Locate the section */
            Element mySection = getElementById(pId);
            if (mySection != null) {
                /* Access parent node */
                Node myParent = mySection.getParentNode();

                /* Restore the child */
                myParent.replaceChild(myHidden, mySection);

                /* Remove the element from the map */
                theMap.remove(pId);
            }

            /* Return the new text */
            return formatXML();
        }

        /* Just return the current text */
        return theText;
    }

    /**
     * Format XML.
     * @return the formatted XML
     */
    public String formatXML() {
        /* Protect against exceptions */
        try {
            /* Transform the new document */
            StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));
            theText = myWriter.getBuffer().toString().replaceAll("\n|\r", "");

            /* Return the new text */
            return theText;
        } catch (TransformerException e) {
            return null;
        }
    }
}
